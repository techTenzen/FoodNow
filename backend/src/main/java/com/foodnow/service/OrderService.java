 
package com.foodnow.service;

import com.foodnow.model.*;
import com.foodnow.repository.OrderRepository;
import com.foodnow.repository.UserRepository;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Order placeOrderFromCart() {
        Cart cart = cartService.getCartForCurrentUser();
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart.");
        }

        Order order = new Order();
        order.setCustomer(cart.getUser());
        order.setRestaurant(cart.getItems().get(0).getFoodItem().getRestaurant());
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFoodItem(cartItem.getFoodItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getFoodItem().getPrice()); // Lock in the price
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        double totalPrice = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setTotalPrice(totalPrice);

        // Clear the cart after placing the order
        cartService.clearCart();

        return orderRepository.save(order);
    }

    public List<Order> getMyOrders() {
        User currentUser = getCurrentUser();
        return orderRepository.findByCustomerId(currentUser.getId());
    }
    
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
