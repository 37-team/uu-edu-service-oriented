package uu.app.order_service.controller;

import uu.app.order_service.entity.OrderEntity;
import uu.app.order_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public OrderEntity createOrder(@RequestHeader("Authorization") String authorizationHeader,
                                   @RequestBody OrderEntity order) {

        return orderService.createOrder(order, authorizationHeader);
    }
}
