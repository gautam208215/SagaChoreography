package com.saga.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.service.PaymentService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumerEvent {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order-event" , groupId = "order-event-group")
    public void paymentConsumer(ConsumerRecord<String, String> consumerRecord) {
        OrderEvent orderEvent;
        try {
            orderEvent = objectMapper.readValue(consumerRecord.value(), OrderEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())){
            PaymentEvent paymentEvent = paymentService.newOrderEvent(orderEvent);
            String paymentStatus;
            try {
                paymentStatus = objectMapper.writeValueAsString(paymentEvent);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            kafkaTemplate.send("payment-event" , paymentStatus);
        }else{
            paymentService.cancellOrderEvent(orderEvent);
        }

    }

}
