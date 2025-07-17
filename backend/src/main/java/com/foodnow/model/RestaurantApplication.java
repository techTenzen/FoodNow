package com.foodnow.model;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_applications")
public class RestaurantApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String restaurantName;

    @Column(nullable = false)
    private String restaurantAddress;

    @Column(nullable = false)
    private String restaurantPhone;

    @OneToOne
    @JoinColumn(name = "applicant_user_id", referencedColumnName = "id")
    private User applicant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    // Add this field for rejection reason
    private String rejectionReason;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    
    public String getRestaurantAddress() { return restaurantAddress; }
    public void setRestaurantAddress(String restaurantAddress) { this.restaurantAddress = restaurantAddress; }
    
    public String getRestaurantPhone() { return restaurantPhone; }
    public void setRestaurantPhone(String restaurantPhone) { this.restaurantPhone = restaurantPhone; }
    
    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }
    
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    // Add getter and setter for rejectionReason
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}