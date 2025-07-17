package com.foodnow.dto;

import java.util.List;

// DTO for returning a restaurant with its menu
public class RestaurantDto {
    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String locationPin;
    private List<FoodItemDto> menu;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getLocationPin() { return locationPin; }
    public void setLocationPin(String locationPin) { this.locationPin = locationPin; }
    public List<FoodItemDto> getMenu() { return menu; }
    public void setMenu(List<FoodItemDto> menu) { this.menu = menu; }
}