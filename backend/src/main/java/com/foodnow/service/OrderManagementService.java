package com.foodnow.service;

import com.foodnow.exception.ResourceNotFoundException;
import com.foodnow.model.Order;
import com.foodnow.model.OrderStatus;
import com.foodnow.model.User;
import com.foodnow.repository.OrderRepository;
import com.foodnow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class OrderManagementService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;

    public List<Order> getOrdersForRestaurant(int restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public List<Order> getOrdersForDeliveryPersonnel(int deliveryPersonnelId) {
        return orderRepository.findByDeliveryPersonnelId(deliveryPersonnelId);
    }

    @Transactional
    public Order updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public void assignDeliveryPersonnel(int orderId, int deliveryPersonnelId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        User deliveryPersonnel = userRepository.findById(deliveryPersonnelId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery personnel not found with ID: " + deliveryPersonnelId));

        order.setDeliveryPersonnel(deliveryPersonnel);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY); 
        
        orderRepository.save(order);
    }
}
