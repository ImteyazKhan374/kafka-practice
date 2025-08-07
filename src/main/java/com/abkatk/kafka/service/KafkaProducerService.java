package com.abkatk.kafka.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.abkatk.kafka.model.Message;
import com.abkatk.kafka.util.AppConstants;

@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    // KafkaTemplate is provided by Spring Kafka for sending messages
    private final KafkaTemplate<String, String> kafkaTemplate;
    // ObjectMapper for converting Message objects to JSON strings
    private final ObjectMapper objectMapper;

    // Constructor injection for KafkaTemplate and ObjectMapper
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a message to the Kafka topic.
     * The message is wrapped in a Message object, serialized to JSON, and then sent.
     * Uses CompletableFuture to handle the asynchronous result of sending,
     * providing callbacks for success and failure.
     *
     * @param messageContent The content string for the message.
     * @throws JsonProcessingException If there's an error during JSON serialization.
     */
    public void sendMessage(String messageContent) throws JsonProcessingException {
        // Create a Message object with a unique ID and the provided content
        Message message = new Message(UUID.randomUUID().toString(), messageContent);

        // Convert the Message object to a JSON string
        String jsonMessage = objectMapper.writeValueAsString(message);

        LOGGER.info(String.format("Sending message -> %s", jsonMessage));

        // Asynchronously send the JSON message to the specified topic
        // The key is null as we are not using a specific key for partitioning in this example.
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(AppConstants.TOPIC_NAME, jsonMessage);

        // Add a callback to handle success or failure of the send operation
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // Log success details including offset and partition
                LOGGER.info("Successfully sent message=[" + jsonMessage +
                        "] with offset=[" + result.getRecordMetadata().offset() +
                        "] to partition=[" + result.getRecordMetadata().partition() + "]");
            } else {
                // Log failure details
                LOGGER.error("Failed to send message=[" +
                        jsonMessage + "] due to: " + ex.getMessage());
            }
        });
    }
}