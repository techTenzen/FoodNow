package com.foodnow.repository;

import com.foodnow.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    // This repository is also mainly used through the Cart entity.
}