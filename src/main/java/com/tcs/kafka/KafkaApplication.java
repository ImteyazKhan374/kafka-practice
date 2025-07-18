package com.tcs.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class KafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}
	

    /**
     * Provides a singleton ObjectMapper bean for JSON serialization/deserialization.
     * This is an industry standard practice to reuse ObjectMapper instances for performance.
     *
     * @return An ObjectMapper instance.
     */
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
