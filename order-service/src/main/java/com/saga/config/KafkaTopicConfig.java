package com.saga.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {


    @Bean
    public NewTopic orderEventTopic() {
        return TopicBuilder.name("order-event")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name("payment-event")
                .partitions(1)
                .replicas(1)
                .build();
    }
}