package com.saga.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saga.dto.OrderRequestDto;
import com.saga.event.OrderEvent;
import com.saga.event.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class OrderStatusPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusPublisher.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishOrderEvent(OrderRequestDto orderRequestDto , OrderStatus orderStatus) {
        OrderEvent orderEvent = new OrderEvent(orderRequestDto, orderStatus);
        String event;
        try {
            event = objectMapper.writeValueAsString(orderEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        ListenableFuture<SendResult<String, String>> send = kafkaTemplate.send("order-event", event);
         send.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
           @Override
           public void onFailure(Throwable ex) {
                //to be handled
           }

           @Override
           public void onSuccess(SendResult<String, String> result) {
               log.info("message sent to partition metadata record :" + result.getRecordMetadata().partition());
               log.info("message sent with offset metadata record :" + result.getRecordMetadata().offset());
           }
       });
     }

}
