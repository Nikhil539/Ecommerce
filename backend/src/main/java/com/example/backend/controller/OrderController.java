package com.example.backend.controller;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.UpdateOrderStatusRequest;
import com.example.backend.model.Order;
import com.example.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request,
                                             Authentication authentication){
        String userEmail = authentication.getName();
        Order order = orderService.createOrder(userEmail,request);

        return ResponseEntity.ok(order);
    }

    @GetMapping

    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication){
        String userEmail = authentication.getName();
        List<Order> orders = orderService.getOrdersByUser(userEmail);

        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        Order order = orderService.updateOrderStatus(
                orderId,
                request.getStatus()
        );

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/cancel")

    public ResponseEntity<Order> cancelOrder(@PathVariable String orderid,Authentication authentication){
        Order order = orderService.cancelOrder(orderid, authentication.getName());
        return ResponseEntity.ok(order);
    }
}
