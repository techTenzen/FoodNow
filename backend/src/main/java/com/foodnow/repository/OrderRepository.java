package com.foodnow.repository;

import com.foodnow.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    // Find all orders placed by a specific customer
    List<Order> findByCustomerId(int customerId);
}