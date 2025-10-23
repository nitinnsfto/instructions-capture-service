package com.example.instructions.service;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.example.instructions.model.PlatformTrade;

public class KafkaPublisher {
	
	public static final String KAFKA_TOPIC="instructions.outbound";
	
	ConcurrentHashMap map = new ConcurrentHashMap();

	public void publish(List<PlatformTrade> trades) throws Exception {

	        Properties props = new Properties();
	        props.put("bootstrap.servers", "localhost:9092"); 
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

	
}