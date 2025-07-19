package com.foodnow.service;

import com.foodnow.dto.CartDto;
import com.foodnow.dto.CartItemDto;
import com.foodnow.dto.FoodItemDto;
import com.foodnow.exception.ResourceNotFoundException;
import com.foodnow.model.*;
import com.foodnow.repository.*;
import com.foodnow.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private FoodItemRepository foodItemRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public CartDto getCartForCurrentUser() {
        User currentUser = getCurrentUser();
        Cart cart = cartRepository.findByUserId(currentUser.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(currentUser);
            return cartRepository.save(newCart);
        });
        return convertToDto(cart);
    }

    @Transactional
    public CartDto addItemToCart(int foodItemId, int quantity) {
        Cart cart = getCartEntity();
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        // Check if trying to order from different restaurant
        if (!cart.getItems().isEmpty() && 
            cart.getItems().get(0).getFoodItem().getRestaurant().getId() != foodItem.getRestaurant().getId()) {
            throw new IllegalStateException("You can only order from one restaurant at a time. Please clear your cart first.");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
            .filter(item -> item.getFoodItem().getId() == foodItemId)
            .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setFoodItem(foodItem);
            newItem.setQuantity(quantity);
            CartItem savedItem = cartItemRepository.save(newItem);
            cart.getItems().add(savedItem);
        }
        
        Cart updatedCart = updateCartTotal(cart);
        return convertToDto(updatedCart);
    }

    @Transactional
    public CartDto updateItemQuantity(int cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        if (cartItem.getCart().getUser().getId() != getCurrentUser().getId()) {
            throw new SecurityException("Unauthorized access to cart item");
        }
        
        if (quantity <= 0) {
            // If quantity is 0 or negative, remove the item
            return removeItemFromCart(cartItemId);
        }
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        Cart updatedCart = updateCartTotal(cartItem.getCart());
        return convertToDto(updatedCart);
    }

    @Transactional
    public CartDto removeItemFromCart(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        
        if (cartItem.getCart().getUser().getId() != getCurrentUser().getId()) {
            throw new SecurityException("Unauthorized access to cart item");
        }
        
        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        Cart updatedCart = updateCartTotal(cart);
        return convertToDto(updatedCart);
    }

    @Transactional
    public CartDto clearCart() {
        Cart cart = getCartEntity();
        cart.getItems().clear();
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(0.0);
        Cart savedCart = cartRepository.save(cart);
        return convertToDto(savedCart);
    }

    // Helper method to get cart entity (for internal use)
    private Cart getCartEntity() {
        User currentUser = getCurrentUser();
        return cartRepository.findByUserId(currentUser.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(currentUser);
            return cartRepository.save(newCart);
        });
    }

    // Public method for other services to get Cart entity
    public Cart getCartEntityForCurrentUser() {
        return getCartEntity();
    }

    private Cart updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
            .mapToDouble(item -> item.getFoodItem().getPrice() * item.getQuantity())
            .sum();
        cart.setTotalPrice(total);
        return cartRepository.save(cart);
    }
    
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Convert Cart entity to CartDto
    private CartDto convertToDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setTotalPrice(cart.getTotalPrice());
        
        dto.setItems(cart.getItems().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList()));
        
        return dto;
    }

    // Convert CartItem entity to CartItemDto
    private CartItemDto convertToDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setFoodItem(convertToDto(cartItem.getFoodItem()));
        return dto;
    }

    // Convert FoodItem entity to FoodItemDto
    private FoodItemDto convertToDto(FoodItem foodItem) {
        FoodItemDto dto = new FoodItemDto();
        dto.setId(foodItem.getId());
        dto.setName(foodItem.getName());
        dto.setDescription(foodItem.getDescription());
        dto.setPrice(foodItem.getPrice());
        dto.setImageUrl(foodItem.getImageUrl());
        dto.setAvailable(foodItem.isAvailable());
        // Only include restaurant name to avoid circular reference
        if (foodItem.getRestaurant() != null) {
            dto.setRestaurantName(foodItem.getRestaurant().getName());
            dto.setRestaurantId(foodItem.getRestaurant().getId());
        }
        return dto;
    }
}