package com.foodnow.service;

import com.foodnow.exception.ResourceNotFoundException;
import com.foodnow.model.*;
import com.foodnow.repository.*;
import com.foodnow.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CartService cartService;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private FoodItemRepository foodItemRepository;

    @Transactional
    public Order placeOrderFromCart() {
        User currentUser = getCurrentUser();
        
        // Get the actual Cart entity (not DTO) for order processing
        Cart cart = cartService.getCartEntityForCurrentUser();

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart.");
        }

        Order order = new Order();
        order.setCustomer(currentUser);
        order.setRestaurant(cart.getItems().get(0).getFoodItem().getRestaurant());
        order.setTotalPrice(cart.getTotalPrice());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderTime(LocalDateTime.now());

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFoodItem(cartItem.getFoodItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getFoodItem().getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        
        // Clear the cart after order is placed
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear(); // Clear the items list
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return savedOrder;
    }

    public List<Order> getMyOrders() {
        User currentUser = getCurrentUser();
        return orderRepository.findByCustomerId(currentUser.getId());
    }

    // Helper method removed - now using CartService method
    
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}