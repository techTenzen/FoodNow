package com.foodnow.service;

import com.foodnow.dto.FoodItemDto;
import com.foodnow.dto.RestaurantDto;
import com.foodnow.exception.ResourceNotFoundException;
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
        // In a real app, you'd filter by an "ACTIVE" status
        return restaurantRepository.findAll().stream()
                // This now uses the more efficient helper that omits the menu
                .map(this::toRestaurantDtoSimple)
                .collect(Collectors.toList());
    }

    public RestaurantDto getRestaurantWithMenu(int restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + restaurantId));
        // This uses the detailed helper that includes the full menu
        return toRestaurantDtoWithMenu(restaurant);
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

    /**
     * Helper to convert a Restaurant to a DTO *with* its full menu.
     * Used when fetching details for a single restaurant.
     */
    private RestaurantDto toRestaurantDtoWithMenu(Restaurant restaurant) {
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

    /**
     * Helper to convert a Restaurant to a DTO *without* its menu.
     * Used for the list of all restaurants to improve performance.
     */
    private RestaurantDto toRestaurantDtoSimple(Restaurant restaurant) {
        RestaurantDto dto = new RestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setPhoneNumber(restaurant.getPhoneNumber());
        dto.setLocationPin(restaurant.getLocationPin());
        // Note: The menu is intentionally not set here.
        return dto;
    }
}
