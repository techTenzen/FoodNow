package com.foodnow.repository;

import com.foodnow.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(int customerId);
    
    // Added queries for Module 5
    List<Order> findByRestaurantId(int restaurantId);
    List<Order> findByDeliveryPersonnelId(int deliveryPersonnelId);
}