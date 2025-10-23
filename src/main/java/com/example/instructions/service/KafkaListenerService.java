package com.example.instructions.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.apache.kafka.common.serialization.StringSerializer;



@Service
public class KafkaListenerService{
	
	   Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);
	   
		   
		@Value("${com.example.instructions.kafka.inbound}")
		private String KAFKA_TOPIC;
		
		@Value("${com.example.instructions.kafka.outbound}")
		private String OUTBOUND_KAFKA_TOPIC;
		
		@Value("${com.example.instructions.kafka.bootstrap.servers}")
		private String BOOTSTRAP_SERVERS;
		
	   
	   public static final String CONSUMER_GROUP = "";
		
	   @Value("${com.example.instructions.kafka.transid}")
	   private String TRANSACTIONAL_ID;
	   
	//@Async
    public void listen() {
		logger.info("Starting Kafka Listener ");
        // Consumer and Producer properties (as defined above)
        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        consumerProps.setProperty("group.id", CONSUMER_GROUP);
        consumerProps.setProperty("enable.auto.commit", "false");
        consumerProps.setProperty("isolation.level", "read_committed");
        consumerProps.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        producerProps.setProperty("enable.idempotence", "true");
        producerProps.setProperty("transactional.id", TRANSACTIONAL_ID);
        producerProps.setProperty("acks", "all");
        producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

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
                            logger.info("Processing record: offset = %d, key = %s, value = %s%n",
                                    record.offset(), record.key(), record.value());

                            // 2. Produce output messages (if applicable)
                            producer.send(new ProducerRecord<>(OUTBOUND_KAFKA_TOPIC, record.key(), "Processed: " + record.value()));
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
                        logger.error("Transaction failed, aborting: " + e.getMessage());
                        producer.abortTransaction();
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
           logger.error("Error in Kafka listener: " + e.getMessage());
        }
    }
}