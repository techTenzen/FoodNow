package com.foodnow.service;

import com.foodnow.exception.ResourceNotFoundException;
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

    @Autowired private RestaurantRepository restaurantRepository;
    @Autowired private FoodItemRepository foodItemRepository;
    @Autowired private UserRepository userRepository;

    public Restaurant getRestaurantByOwnerId(int ownerId) {
        return restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found for owner ID: " + ownerId));
    }

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userDetails.getId()));
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

        Restaurant currentRestaurant = getRestaurantByCurrentOwner();
        if (existingItem.getRestaurant().getId() != currentRestaurant.getId()) {
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

        Restaurant currentRestaurant = getRestaurantByCurrentOwner();
        if (item.getRestaurant().getId() != currentRestaurant.getId()) {
            throw new SecurityException("Unauthorized to delete this food item");
        }

        foodItemRepository.delete(item);
    }

    @Transactional
    public FoodItem toggleFoodItemAvailability(int itemId) {
        FoodItem item = foodItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + itemId));

        Restaurant currentRestaurant = getRestaurantByCurrentOwner();
        if (item.getRestaurant().getId() != currentRestaurant.getId()) {
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
}
