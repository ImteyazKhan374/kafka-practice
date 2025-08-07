package com.abkatk.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.abkatk.kafka.util.AppConstants;

@Configuration
public class KafkaTopicConfig {

	/**
	 * Defines a new Kafka topic. This bean will automatically create the topic if
	 * it doesn't exist when the Spring Boot application starts, based on the
	 * configured partitions and replicas.
	 *
	 * @return A NewTopic instance for 'my-spring-boot-topic'.
	 */
	@Bean
	NewTopic myTopic() {
		return TopicBuilder.name(AppConstants.TOPIC_NAME).partitions(3) // Number of partitions for the topic. More
																		// partitions allow for more parallelism.
				.replicas(1) // Number of replicas for the topic. 1 is suitable for a single-node Kafka
								// setup.
								// In production, typically 3 or more for fault tolerance.
				.build();
	}
}