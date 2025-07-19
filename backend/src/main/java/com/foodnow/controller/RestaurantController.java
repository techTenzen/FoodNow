package com.foodnow.controller;

import com.foodnow.dto.FoodItemDto;
import com.foodnow.dto.RestaurantDashboardDto;
import com.foodnow.dto.RestaurantDto;
import com.foodnow.model.FoodItem;
import com.foodnow.model.Restaurant;
import com.foodnow.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurant")
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // --- Single endpoint for the entire dashboard ---
    @GetMapping("/dashboard")
    public ResponseEntity<RestaurantDashboardDto> getDashboardData() {
        return ResponseEntity.ok(restaurantService.getDashboardData());
    }

    // --- Restaurant Profile Management ---
    @GetMapping("/profile")
    public ResponseEntity<RestaurantDto> getRestaurantProfile() {
        Restaurant restaurant = restaurantService.getRestaurantByCurrentOwner();
        return ResponseEntity.ok(toRestaurantDto(restaurant));
    }

    @PutMapping("/profile")
    public ResponseEntity<RestaurantDto> updateRestaurantProfile(@RequestBody Restaurant restaurant) {
        Restaurant updatedRestaurant = restaurantService.updateRestaurantProfile(restaurant);
        return ResponseEntity.ok(toRestaurantDto(updatedRestaurant));
    }

    // --- Menu Management ---
    @GetMapping("/menu")
    public ResponseEntity<List<FoodItemDto>> getMenu() {
        List<FoodItem> menu = restaurantService.getMenuByCurrentOwner();
        List<FoodItemDto> dtoList = menu.stream()
                                      .map(this::toFoodItemDto)
                                      .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/menu")
    public ResponseEntity<FoodItemDto> addFoodItem(@RequestBody FoodItem foodItem) {
        FoodItem newFoodItem = restaurantService.addFoodItem(foodItem);
        return ResponseEntity.ok(toFoodItemDto(newFoodItem));
    }

    @PutMapping("/menu/{itemId}")
    public ResponseEntity<FoodItemDto> updateFoodItem(@PathVariable int itemId, @RequestBody FoodItem foodItem) {
        FoodItem updatedItem = restaurantService.updateFoodItem(itemId, foodItem);
        return ResponseEntity.ok(toFoodItemDto(updatedItem));
    }

    @PatchMapping("/menu/{itemId}/availability")
    public ResponseEntity<FoodItemDto> toggleFoodItemAvailability(@PathVariable int itemId) {
        FoodItem updatedItem = restaurantService.toggleFoodItemAvailability(itemId);
        return ResponseEntity.ok(toFoodItemDto(updatedItem));
    }

    @DeleteMapping("/menu/{itemId}")
    public ResponseEntity<String> deleteFoodItem(@PathVariable int itemId) {
        restaurantService.deleteFoodItem(itemId);
        return ResponseEntity.ok("Food item deleted successfully.");
    }

    // --- Helper Methods for DTO Conversion ---
    private FoodItemDto toFoodItemDto(FoodItem item) {
        FoodItemDto dto = new FoodItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        dto.setImageUrl(item.getImageUrl());
        dto.setAvailable(item.isAvailable());
        return dto;
    }

    private RestaurantDto toRestaurantDto(Restaurant restaurant) {
        RestaurantDto dto = new RestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhoneNumber(restaurant.getPhoneNumber());
        dto.setLocationPin(restaurant.getLocationPin());
        
        List<FoodItemDto> menuDto = restaurant.getMenu().stream()
                                            .map(this::toFoodItemDto)
                                            .collect(Collectors.toList());
        dto.setMenu(menuDto);
        return dto;
    }
}
