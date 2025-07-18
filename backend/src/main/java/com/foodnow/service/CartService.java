// File: src/main/java/com/foodnow/service/CartService.java
package com.foodnow.service;

import com.foodnow.model.Cart;
import com.foodnow.model.CartItem;
import com.foodnow.model.FoodItem;
import com.foodnow.model.User;
import com.foodnow.repository.CartRepository;
import com.foodnow.repository.FoodItemRepository;
import com.foodnow.repository.UserRepository;
import com.foodnow.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Transactional
    public Cart getCartForCurrentUser() {
        User currentUser = getCurrentUser();
        // Find cart by user ID, or create a new one if it doesn't exist
        return cartRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(currentUser);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addItemToCart(int foodItemId, int quantity) {
        Cart cart = getCartForCurrentUser();
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        // Check if the item is from a different restaurant
        if (!cart.getItems().isEmpty() &&
            cart.getItems().get(0).getFoodItem().getRestaurant().getId() != foodItem.getRestaurant().getId()) {
            throw new IllegalStateException("You can only order from one restaurant at a time. Please clear your cart first.");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getFoodItem().getId() == foodItemId)
                .findFirst();

        if (existingItemOpt.isPresent()) {
            // Update quantity
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Add new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setFoodItem(foodItem);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart clearCart() {
        Cart cart = getCartForCurrentUser();
        cart.getItems().clear();
        return cartRepository.save(cart);
    }

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
