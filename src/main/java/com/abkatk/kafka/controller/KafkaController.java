package com.abkatk.kafka.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.abkatk.kafka.service.KafkaProducerService;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

	private final KafkaProducerService kafkaProducerService;

	// Constructor injection for KafkaProducerService
	public KafkaController(KafkaProducerService kafkaProducerService) {
		this.kafkaProducerService = kafkaProducerService;
	}

	/**
	 * REST endpoint to send a message to Kafka. Example usage: GET
	 * http://localhost:8080/api/kafka/publish?message=hello_kafka
	 *
	 * @param message The message content to send.
	 * @return A ResponseEntity indicating success or failure.
	 */
	@GetMapping("/publish")
	public ResponseEntity<String> publishMessage(@RequestParam("message") String message) {
		try {
			kafkaProducerService.sendMessage(message);
			return ResponseEntity.ok("Message sent to Kafka topic: " + message);
		} catch (JsonProcessingException e) {
			// Handle JSON serialization errors
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error sending message: " + e.getMessage());
		} catch (Exception e) {
			// Catch any other unexpected errors during message sending
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}
}
