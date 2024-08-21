package com.saga.service;

import com.saga.dto.OrderRequestDto;
import com.saga.entity.PurchaseOrder;
import com.saga.event.OrderStatus;
import com.saga.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;


    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder order = orderRepository.save(convertToEntity(orderRequestDto));
        orderRequestDto.setOrderId(order.getId());
        orderStatusPublisher.publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);
        return order;
    }

    public PurchaseOrder convertToEntity(OrderRequestDto orderRequestDto){
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setUserId(orderRequestDto.getUserId());
        purchaseOrder.setPrice(orderRequestDto.getAmount());
        purchaseOrder.setProductId(orderRequestDto.getProductId());
        purchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
        return purchaseOrder;
    }

    public List<PurchaseOrder> getAllOrders(){
        return orderRepository.findAll();
    }
}
