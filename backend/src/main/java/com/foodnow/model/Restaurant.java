package com.foodnow.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {
// Add to Restaurant.java
@Enumerated(EnumType.STRING)
private RestaurantStatus status = RestaurantStatus.ACTIVE; // ACTIVE, INACTIVE, SUSPENDED

public enum RestaurantStatus {
    ACTIVE, INACTIVE, SUSPENDED
}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

 @Column(nullable = false) // Add this annotation
    private String locationPin; // Add this new field
    @OneToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodItem> menu = new ArrayList<>();

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public List<FoodItem> getMenu() { return menu; }
    public void setMenu(List<FoodItem> menu) { this.menu = menu; }
    public String getLocationPin() { return locationPin; }
    public void setLocationPin(String locationPin) { this.locationPin = locationPin; }
}