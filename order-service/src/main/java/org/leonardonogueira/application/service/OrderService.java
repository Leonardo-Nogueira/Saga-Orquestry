package org.leonardonogueira.application.service;

import lombok.AllArgsConstructor;
import org.leonardonogueira.application.document.Event;
import org.leonardonogueira.application.document.Order;
import org.leonardonogueira.application.dto.OrderRequestDTO;
import org.leonardonogueira.application.producer.SagaProducer;
import org.leonardonogueira.application.repository.OrderRepository;
import org.leonardonogueira.application.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private static final String PATTERN_TRANSACTION_ID = "%s_%s";

    private final OrderRepository orderRepository;
    private final JsonUtils jsonUtils;
    private final SagaProducer producer;
    private final EventService eventService;

    public Order create(OrderRequestDTO orderRequest) {

        var transactionId = String.format(PATTERN_TRANSACTION_ID, Instant.now().toEpochMilli(), UUID.randomUUID());

        var order = Order
                    .builder()
                    .productOrders(orderRequest.getProducts())
                    .createdAt(LocalDateTime.now())
                    .transactionId(transactionId)
                    .build();


        orderRepository.save(order);

        var payloadEvent = createPayload(order);
        var payload = jsonUtils.toJson(payloadEvent);

        producer.sendEvent(payload);

        return order;
    }

    private Event createPayload(Order order) {

        var event = Event
                .builder()
                .orderId(order.getId())
                .createdAt(LocalDateTime.now())
                .transactionId(order.getTransactionId())
                .payload(order)
                .build();

        eventService.saveEvent(event);

        return event;

    }
}
