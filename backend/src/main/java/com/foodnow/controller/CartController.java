// File: src/main/java/com/foodnow/controller/CartController.java
package com.foodnow.controller;

import com.foodnow.dto.CartDto;
import com.foodnow.dto.CartItemDto;
import com.foodnow.dto.FoodItemDto;
import com.foodnow.model.Cart;
import com.foodnow.model.CartItem;
import com.foodnow.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        Cart cart = cartService.getCartForCurrentUser();
        return ResponseEntity.ok(toCartDto(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItemToCart(@RequestBody Map<String, Integer> payload) {
        int foodItemId = payload.get("foodItemId");
        int quantity = payload.get("quantity");
        Cart updatedCart = cartService.addItemToCart(foodItemId, quantity);
        return ResponseEntity.ok(toCartDto(updatedCart));
    }

    @DeleteMapping
    public ResponseEntity<CartDto> clearCart() {
        Cart clearedCart = cartService.clearCart();
        return ResponseEntity.ok(toCartDto(clearedCart));
    }

    // --- DTO Conversion Helper ---
    private CartDto toCartDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        
        List<CartItemDto> itemDtos = cart.getItems().stream().map(item -> {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setId(item.getId());
            itemDto.setQuantity(item.getQuantity());

            FoodItemDto foodDto = new FoodItemDto();
            foodDto.setId(item.getFoodItem().getId());
            foodDto.setName(item.getFoodItem().getName());
            foodDto.setPrice(item.getFoodItem().getPrice());
            itemDto.setFoodItem(foodDto);
            
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        
        double totalPrice = itemDtos.stream()
            .mapToDouble(item -> item.getFoodItem().getPrice() * item.getQuantity())
            .sum();
        dto.setTotalPrice(totalPrice);

        return dto;
    }
}