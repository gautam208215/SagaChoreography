package com.saga.service;

import com.saga.dto.OrderRequestDto;
import com.saga.entity.PurchaseOrder;
import com.saga.event.OrderStatus;
import com.saga.event.PaymentEvent;
import com.saga.event.PaymentStatus;
import com.saga.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class OrderStatusUpdateHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    @Transactional
    public void updateOrder(PaymentEvent paymentEvent) {
        Optional<PurchaseOrder> purchaseOrderOptional = orderRepository.findById(paymentEvent.getPaymentRequestDto().getOrderId());
        purchaseOrderOptional.ifPresent(purchaseOrder -> {
            purchaseOrder.setPaymentStatus(paymentEvent.getPaymentStatus());
            updateOrder(purchaseOrder);
        });
    }


    private void updateOrder(PurchaseOrder purchaseOrder){
        boolean isPaymentCompleted = purchaseOrder.getPaymentStatus().equals(PaymentStatus.PAYMENT_SUCCESS);
        OrderStatus orderStatus = isPaymentCompleted ? OrderStatus.ORDER_COMPLETED : OrderStatus.ORDER_CANCELLED;
        purchaseOrder.setOrderStatus(orderStatus);
        if(!isPaymentCompleted){
            orderStatusPublisher.publishOrderEvent(convertEntityToDto(purchaseOrder), orderStatus);
        }
    }

    public OrderRequestDto convertEntityToDto(PurchaseOrder purchaseOrder) {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setOrderId(purchaseOrder.getId());
        orderRequestDto.setUserId(purchaseOrder.getUserId());
        orderRequestDto.setAmount(purchaseOrder.getPrice());
        orderRequestDto.setProductId(purchaseOrder.getProductId());
        return orderRequestDto;
    }
}
