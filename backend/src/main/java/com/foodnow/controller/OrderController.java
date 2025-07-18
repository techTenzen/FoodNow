package com.foodnow.controller;

import com.foodnow.dto.OrderDto;
import com.foodnow.dto.OrderItemDto;
import com.foodnow.model.Order;
import com.foodnow.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder() {
        try {
            Order order = orderService.placeOrderFromCart();
            return ResponseEntity.ok(toOrderDto(order));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        List<Order> orders = orderService.getMyOrders();
        List<OrderDto> dtoList = orders.stream().map(this::toOrderDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
    
    // --- DTO Conversion Helper ---
    private OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setRestaurantName(order.getRestaurant().getName());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        dto.setOrderTime(order.getOrderTime());

        List<OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setFoodItemName(item.getFoodItem().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            return itemDto;
        }).collect(Collectors.toList());
        
        dto.setItems(itemDtos);
        return dto;
    }
}
