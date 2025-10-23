package com.example.instructions.service;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.instructions.model.PlatformTrade;


@Component
public class KafkaPublisher {
	
	Logger logger = LoggerFactory.getLogger(KafkaPublisher.class);
	   
	@Value("${com.example.instructions.kafka.outbound}")
	private String KAFKA_TOPIC;
	
	@Value("${com.example.instructions.kafka.bootstrap.servers}")
	private String BOOTSTRAP_SERVERS;
	
	
	ConcurrentHashMap map = new ConcurrentHashMap();

	public void publish(List<PlatformTrade> trades) throws Exception {
			
			simpleSend(trades);

	        Properties props = new Properties();
	        props.put("bootstrap.servers", BOOTSTRAP_SERVERS); 
	        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
	        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

	        // Enable idempotence for exactly-once guarantees within a single producer session
	        props.put("enable.idempotence", "true"); 
	        // These are automatically set when enable.idempotence is true, but good to know
	        // props.put("acks", "all");
	        // props.put("retries", Integer.MAX_VALUE);
	        // props.put("max.in.flight.requests.per.connection", "5"); 

	        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
	            for (int i = 0; i < trades.size(); i++) {
	                String key = "key-" + i;
	                String value = trades.get(i).toString();
	                
	                logger.info(value + " " + KAFKA_TOPIC + " " + BOOTSTRAP_SERVERS);
	                
	                ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, key, value);
	                map.put(key, value);
	                producer.send(record, (metadata, exception) -> {
	                    if (exception == null) {
	                        System.out.printf("Sent record (key=%s, value=%s) to topic %s, partition %d, offset %d%n",
	                                key, value, metadata.topic(), metadata.partition(), metadata.offset());
	                    } else {
	                        System.err.println("Error sending record: " + exception.getMessage());
	                    }
	                });
	            }
	            producer.flush(); // Ensure all messages are sent
	        } catch (Exception e) {
	        	
	        	// Retry Logic
	        	
	        	
	            throw new Exception();
	            
	           
	        }
		
	}

	private void simpleSend(List<PlatformTrade> trades) {
		
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS);
        //properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        
        String key ="key1";
        String value = "Hello Kafka from Java!";
        
        ProducerRecord<String, String> record = new ProducerRecord<>(KAFKA_TOPIC, key, value);

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                System.out.printf("Sent record (key=%s, value=%s) to topic %s, partition %d, offset %d%n",
                        key, value, metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                System.err.println("Error sending record: " + exception.getMessage());
            }
        });
        
        producer.flush();
        producer.close();
        System.out.println("Message sent!");
		
	}

	
}