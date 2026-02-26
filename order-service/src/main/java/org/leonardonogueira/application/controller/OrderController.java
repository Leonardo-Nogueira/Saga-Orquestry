package org.leonardonogueira.application.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.leonardonogueira.application.document.Order;
import org.leonardonogueira.application.dto.OrderRequestDTO;
import org.leonardonogueira.application.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequestDTO orderRequest) {
        log.info("Received order request: {}", orderRequest);
        return orderService.create(orderRequest);
    }

}
