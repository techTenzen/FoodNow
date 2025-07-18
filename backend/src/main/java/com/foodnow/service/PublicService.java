package com.foodnow.service;

import com.foodnow.dto.FoodItemDto;
import com.foodnow.dto.RestaurantDto;
import com.foodnow.model.FoodItem;
import com.foodnow.model.Restaurant;
import com.foodnow.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<RestaurantDto> getAllActiveRestaurants() {
        return restaurantRepository.findAll().stream()
                // In a real app, you'd filter by an "ACTIVE" status
                .map(this::toRestaurantDto)
                .collect(Collectors.toList());
    }

    public RestaurantDto getRestaurantWithMenu(int restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with ID: " + restaurantId));
        return toRestaurantDto(restaurant);
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
        
        // Convert the menu items to DTOs, but only include available items
        List<FoodItemDto> menuDto = restaurant.getMenu().stream()
                                              .filter(FoodItem::isAvailable)
                                              .map(this::toFoodItemDto)
                                              .collect(Collectors.toList());
        dto.setMenu(menuDto);
        return dto;
    }
}
