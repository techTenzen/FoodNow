package com.foodnow.service;

import com.foodnow.dto.FoodItemDto;
import com.foodnow.dto.OrderDto;
import com.foodnow.dto.OrderItemDto;
import com.foodnow.dto.RestaurantDashboardDto;
import com.foodnow.dto.RestaurantDto;
import com.foodnow.exception.ResourceNotFoundException;
import com.foodnow.model.FoodItem;
import com.foodnow.model.Order;
import com.foodnow.model.OrderItem;
import com.foodnow.model.Restaurant;
import com.foodnow.model.User;
import com.foodnow.repository.FoodItemRepository;
import com.foodnow.repository.OrderRepository;
import com.foodnow.repository.RestaurantRepository;
import com.foodnow.repository.UserRepository;
import com.foodnow.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private FoodItemRepository foodItemRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public RestaurantDashboardDto getDashboardData() {
        Restaurant restaurant = getRestaurantByCurrentOwner();
        
        RestaurantDashboardDto dashboardDto = new RestaurantDashboardDto();
        dashboardDto.setRestaurantProfile(toRestaurantDto(restaurant));
        
        List<Order> orders = orderRepository.findByRestaurantId(restaurant.getId());
        dashboardDto.setOrders(orders.stream().map(this::toOrderDto).collect(Collectors.toList()));
        
        List<FoodItem> menu = restaurant.getMenu();
        dashboardDto.setMenu(menu.stream().map(this::toFoodItemDto).collect(Collectors.toList()));
        
        return dashboardDto;
    }

    public Restaurant getRestaurantByOwnerId(int ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found for owner ID: " + ownerId));
    }
    
    public Restaurant getRestaurantByCurrentOwner() {
        User currentUser = getCurrentUser();
        return getRestaurantByOwnerId(currentUser.getId());
    }

    public List<FoodItem> getMenuByCurrentOwner() {
        Restaurant restaurant = getRestaurantByCurrentOwner();
        return restaurant.getMenu();
    }

    @Transactional
    public FoodItem addFoodItem(FoodItem foodItem) {
        Restaurant restaurant = getRestaurantByCurrentOwner();
        foodItem.setRestaurant(restaurant);
        return foodItemRepository.save(foodItem);
    }

    @Transactional
    public FoodItem updateFoodItem(int itemId, FoodItem updatedItem) {
        FoodItem existingItem = foodItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + itemId));

        if (existingItem.getRestaurant().getId() != getRestaurantByCurrentOwner().getId()) {
            throw new SecurityException("Unauthorized to update this food item");
        }

        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setImageUrl(updatedItem.getImageUrl());
        existingItem.setAvailable(updatedItem.isAvailable());

        return foodItemRepository.save(existingItem);
    }

    @Transactional
    public void deleteFoodItem(int itemId) {
        FoodItem item = foodItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + itemId));
        if (item.getRestaurant().getId() != getRestaurantByCurrentOwner().getId()) {
            throw new SecurityException("Unauthorized to delete this food item");
        }
        foodItemRepository.delete(item);
    }

    @Transactional
    public FoodItem toggleFoodItemAvailability(int itemId) {
        FoodItem item = foodItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + itemId));
        if (item.getRestaurant().getId() != getRestaurantByCurrentOwner().getId()) {
            throw new SecurityException("Unauthorized to update this food item");
        }
        item.setAvailable(!item.isAvailable());
        return foodItemRepository.save(item);
    }
    
    @Transactional
    public Restaurant updateRestaurantProfile(Restaurant updatedRestaurant) {
        Restaurant existingRestaurant = getRestaurantByCurrentOwner();
        existingRestaurant.setName(updatedRestaurant.getName());
        existingRestaurant.setAddress(updatedRestaurant.getAddress());
        existingRestaurant.setPhoneNumber(updatedRestaurant.getPhoneNumber());
        return restaurantRepository.save(existingRestaurant);
    }

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userDetails.getId()));
    }
    
    // --- DTO Helper Methods ---
    private RestaurantDto toRestaurantDto(Restaurant restaurant) {
        RestaurantDto dto = new RestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhoneNumber(restaurant.getPhoneNumber());
        return dto;
    }

    private FoodItemDto toFoodItemDto(FoodItem item) {
        FoodItemDto dto = new FoodItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setAvailable(item.isAvailable());
        dto.setImageUrl(item.getImageUrl());
        return dto;
    }

    private OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto dto = new OrderItemDto();
        dto.setItemName(item.getFoodItem() != null ? item.getFoodItem().getName() : "N/A");
        dto.setQuantity(item.getQuantity());
        return dto;
    }

    private OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomer() != null ? order.getCustomer().getName() : "N/A");
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus());
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList()));
        }
        return dto;
    }
}
