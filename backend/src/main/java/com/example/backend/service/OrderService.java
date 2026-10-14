package com.example.backend.service;

import com.example.backend.dto.OrderItemRequest;
import com.example.backend.dto.OrderRequest;
import com.example.backend.exception.InsufficientStockException;
import com.example.backend.exception.InvalidOrderStatusException;
import com.example.backend.exception.ProductNotFoundException;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.OrderStatus;
import com.example.backend.model.Product;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository
             ) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(String userId, OrderRequest request) {

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    "Product not found: "
                                            + itemRequest.getProductId()
                            ));

            if(product.getStock() < itemRequest.getQuantity()){
                throw new InsufficientStockException("Insufficient stock for product: "
                        + product.getName());
            }

            double itemTotal =
                    product.getPrice() * itemRequest.getQuantity();

            totalAmount += itemTotal;

            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    itemRequest.getQuantity()
            );

            orderItems.add(orderItem);

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);
        }



        Order order = new Order();

        order.setUserId(userId);
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order updateOrderStatus(
            String orderId,
            OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidOrderStatusException(
                    "Cannot change order status from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        // Restore stock when order is cancelled
        if (newStatus == OrderStatus.CANCELLED) {

            for (OrderItem orderItem : order.getItems()) {

                Product product = productRepository
                        .findById(orderItem.getProductId())
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found: "
                                                + orderItem.getProductId()
                                ));

                product.setStock(
                        product.getStock()
                                + orderItem.getQuantity()
                );

                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);

        return orderRepository.save(order);
    }

    private boolean isValidTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        switch (currentStatus) {

            case PLACED:
                return newStatus == OrderStatus.CONFIRMED
                        || newStatus == OrderStatus.CANCELLED;

            case CONFIRMED:
                return newStatus == OrderStatus.SHIPPED;

            case SHIPPED:
                return newStatus == OrderStatus.DELIVERED;

            case DELIVERED:
            case CANCELLED:
                return false;

            default:
                return false;
        }
    }

    public Order cancelOrder(String orderId,String userEmail){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Order not found"
                        ));

        if(!userEmail.equals(order.getUserId())){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,"Order Not Found"
            );
        }

        if(!isValidTransition(order.getStatus(),OrderStatus.CANCELLED)){
            throw new InvalidOrderStatusException("This Order cannot be cancelled from status" + order.getStatus());
        }

        for(OrderItem  item : order.getItems()){
            Product product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ProductNotFoundException("product Not Found" + item.getProductId()));
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);

        }

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);

    }

}