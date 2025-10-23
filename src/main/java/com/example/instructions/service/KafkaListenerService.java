package com.example.instructions.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class KafkaListenerService{
	
	   Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);
	   
	   public static final String CONSUMER_GROUP = "";
	   public static final String TRANSACTIONAL_ID = "";
	   public static final String KAFKA_TOPIC = "";
	   
	
    public static void listen(String[] args) {
        // Consumer and Producer properties (as defined above)
        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", "localhost:9092");
        consumerProps.setProperty("group.id", CONSUMER_GROUP);
        consumerProps.setProperty("enable.auto.commit", "false");
        consumerProps.setProperty("isolation.level", "read_committed");

        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", "localhost:9092");
        producerProps.setProperty("enable.idempotence", "true");
        producerProps.setProperty("transactional.id", TRANSACTIONAL_ID);
        producerProps.setProperty("acks", "all");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
             KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

            consumer.subscribe(Collections.singletonList(KAFKA_TOPIC));
            producer.initTransactions();

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                if (!records.isEmpty()) {
                    producer.beginTransaction();
                    try {
                        for (ConsumerRecord<String, String> record : records) {
                            // 1. Process the message (e.g., transform, store in database)
                            System.out.printf("Processing record: offset = %d, key = %s, value = %s%n",
                                    record.offset(), record.key(), record.value());

                            // 2. Produce output messages (if applicable)
                            producer.send(new ProducerRecord<>("output-topic", record.key(), "Processed: " + record.value()));
                        }

                        // 3. Commit consumer offsets within the same transaction
                        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();
                        records.partitions().forEach(partition -> {
                            long lastOffset = records.records(partition).get(records.records(partition).size() - 1).offset();
                            offsetsToCommit.put(partition, new OffsetAndMetadata(lastOffset + 1));
                        });
                        producer.sendOffsetsToTransaction(offsetsToCommit, consumer.groupMetadata().groupId());

                        // 4. Commit the transaction
                        producer.commitTransaction();
                    } catch (KafkaException e) {
                        // Handle transactional errors (e.g., ProducerFencedException)
                        System.err.println("Transaction failed, aborting: " + e.getMessage());
                        producer.abortTransaction();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in Kafka listener: " + e.getMessage());
        }
    }
}