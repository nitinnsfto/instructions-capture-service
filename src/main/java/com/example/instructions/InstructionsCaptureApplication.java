package com.example.instructions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import com.example.instructions.service.KafkaListenerService;

@SpringBootApplication
@EnableAsync
public class InstructionsCaptureApplication {
	
	@Autowired
	KafkaListenerService kafkaListenerService;
	
	public static void main(String [] args) {
		SpringApplication.run(InstructionsCaptureApplication.class, args);

	}
	
	@Bean
	public CommandLineRunner schedulingRunner(TaskExecutor executor) {
	    return new CommandLineRunner() {
	        public void run(String... args) throws Exception {
	        	kafkaListenerService.listen();
	        };
	    };
	}

}
