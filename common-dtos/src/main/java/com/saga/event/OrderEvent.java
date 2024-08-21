package com.saga.event;

import com.saga.dto.OrderRequestDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Data
public class OrderEvent implements Event{

    private final UUID eventId = UUID.randomUUID();
    private OrderRequestDto orderRequestDto;
    private OrderStatus orderStatus;
    private final Date date = new Date();

    public OrderEvent(OrderRequestDto orderRequestDto, OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        this.orderRequestDto = orderRequestDto;
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public Date getDate() {
        return date;
    }
}
