package com.saga.service;

import com.saga.dto.OrderRequestDto;
import com.saga.dto.PaymentRequestDto;
import com.saga.entity.UserBalance;
import com.saga.entity.UserTransaction;
import com.saga.event.OrderEvent;
import com.saga.event.PaymentEvent;
import com.saga.event.PaymentStatus;
import com.saga.repository.UserBalanceRepository;
import com.saga.repository.UserTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserTransactionRepository userTransactionRepository;

    @PostConstruct
    public void initBalanceInDb(){
        userBalanceRepository.saveAll(Stream.of(new UserBalance(101, 5000),
                new UserBalance(102 , 6000) ,
                new UserBalance(103,4200),
                new UserBalance(104, 20000),
                new UserBalance(105, 1000)).collect(Collectors.toList()));
    }

    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
        OrderRequestDto orderRequestDto = orderEvent.getOrderRequestDto();
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(orderRequestDto.getOrderId() ,
                orderRequestDto.getUserId(), orderRequestDto.getAmount());

       return userBalanceRepository.findById(orderRequestDto.getUserId())
                .filter(ub -> ub.getAmount() > orderRequestDto.getAmount())
                .map(ub -> {
                    ub.setAmount(ub.getAmount() - orderRequestDto.getAmount());
                    userTransactionRepository.save(new UserTransaction(orderRequestDto.getOrderId(),
                            orderRequestDto.getUserId(), orderRequestDto.getAmount()));
                    return new PaymentEvent(paymentRequestDto, PaymentStatus.PAYMENT_SUCCESS);
                }).orElse(new PaymentEvent(paymentRequestDto,PaymentStatus.PAYMENT_FAILED));
    }


    @Transactional
    public void cancellOrderEvent(OrderEvent orderEvent) {
        userTransactionRepository.findById(orderEvent.getOrderRequestDto().getOrderId())
                .ifPresent(ut -> {
                    userTransactionRepository.delete(ut);
                    userBalanceRepository.findById(ut.getUserId())
                            .ifPresent(ub -> ub.setAmount(ub.getAmount() + ut.getAmount()));
                });
    }

}
