package com.foodnow.service;

import com.foodnow.model.FoodItem;
import com.foodnow.model.Restaurant;
import com.foodnow.model.User;
import com.foodnow.repository.FoodItemRepository;
import com.foodnow.repository.RestaurantRepository;
import com.foodnow.repository.UserRepository;
import com.foodnow.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UserRepository userRepository;

    public Restaurant getRestaurantByCurrentOwner() {
        User currentUser = getCurrentUser();
        return restaurantRepository.findByOwnerId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found for current owner"));
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
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        // Verify that the item belongs to the current owner's restaurant
        Restaurant currentRestaurant = getRestaurantByCurrentOwner();
        if (existingItem.getRestaurant().getId() != currentRestaurant.getId()) {
            throw new RuntimeException("Unauthorized to update this food item");
        }

        // Update the fields
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
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        // Verify that the item belongs to the current owner's restaurant
        Restaurant currentRestaurant = getRestaurantByCurrentOwner();
        if (item.getRestaurant().getId() != currentRestaurant.getId()) {
            throw new RuntimeException("Unauthorized to delete this food item");
        }

        foodItemRepository.delete(item);
    }

    @Transactional
    public FoodItem toggleFoodItemAvailability(int itemId) {
        FoodItem item = foodItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        // Verify that the item belongs to the current owner's restaurant
        Restaurant currentRestaurant = getRestaurantByCurrentOwner();
        if (item.getRestaurant().getId() != currentRestaurant.getId()) {
            throw new RuntimeException("Unauthorized to update this food item");
        }

        item.setAvailable(!item.isAvailable());
        return foodItemRepository.save(item);
    }

    @Transactional
    public Restaurant updateRestaurantProfile(Restaurant updatedRestaurant) {
        Restaurant existingRestaurant = getRestaurantByCurrentOwner();

        // Update allowed fields
        existingRestaurant.setName(updatedRestaurant.getName());
        existingRestaurant.setAddress(updatedRestaurant.getAddress());
        existingRestaurant.setPhoneNumber(updatedRestaurant.getPhoneNumber());

        return restaurantRepository.save(existingRestaurant);
    }

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}