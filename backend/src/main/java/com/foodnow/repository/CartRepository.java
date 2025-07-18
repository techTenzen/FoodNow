package com.foodnow.repository;

import com.foodnow.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    // The Cart ID is the same as the User ID, so we can find it this way.
    Optional<Cart> findByUserId(int userId);
}
