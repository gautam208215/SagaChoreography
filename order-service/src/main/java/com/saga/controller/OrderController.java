package com.saga.controller;

import com.saga.dto.OrderRequestDto;
import com.saga.entity.PurchaseOrder;
import com.saga.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public PurchaseOrder purchaseOrder(@RequestBody OrderRequestDto orderRequestDto){
       return orderService.createOrder(orderRequestDto);
    }

    @GetMapping("/orders")
    public List<PurchaseOrder> getAllOrder(){
        return orderService.getAllOrders();
    }
}
