package com.saga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.event.PaymentEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusEvent {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderStatusUpdateHandler orderStatusUpdateHandler;

    @KafkaListener(topics = "payment-event" , groupId = "payment-event-group")
    public void paymentConsumer(ConsumerRecord<String, String> consumerRecord) {
        PaymentEvent paymentEvent;
        try {
            paymentEvent = objectMapper.readValue(consumerRecord.value(), PaymentEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        orderStatusUpdateHandler.updateOrder(paymentEvent);
    }
}
