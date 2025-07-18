package com.tcs.kafka.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.kafka.model.Message;
import com.tcs.kafka.util.AppConstants;

@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;

    // Constructor injection for ObjectMapper
    public KafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Listens for messages on the specified Kafka topic and consumer group.
     * The @KafkaListener annotation handles message consumption automatically.
     * The consumed message (which is a JSON string) is deserialized into a Message object.
     *
     * @param messageJson The consumed message as a JSON string.
     */
    @KafkaListener(topics = AppConstants.TOPIC_NAME, groupId = AppConstants.GROUP_ID)
    public void consume(String messageJson) {
        try {
            // Deserialize the JSON string back into a Message object
            Message message = objectMapper.readValue(messageJson, Message.class);
            LOGGER.info(String.format("Message received -> ID: %s, Content: %s", message.getId(), message.getContent()));
            // Here you would typically process the message, e.g., save to database,
            // perform business logic, call other services, etc.
        } catch (Exception e) {
            LOGGER.error("Error deserializing message: " + messageJson, e);
            // In a real-world scenario, you might send this message to a Dead Letter Queue (DLQ)
            // or trigger an alert.
        }
    }
}