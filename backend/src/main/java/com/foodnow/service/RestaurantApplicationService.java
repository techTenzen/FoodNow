package com.foodnow.service;

import com.foodnow.model.*;
import com.foodnow.repository.RestaurantApplicationRepository;
import com.foodnow.dto.RestaurantApplicationRequest; // <-- Import the new DTO

import com.foodnow.repository.RestaurantRepository;
import com.foodnow.repository.UserRepository;
import com.foodnow.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantApplicationService {

    @Autowired
    private RestaurantApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

   public RestaurantApplication applyForRestaurant(RestaurantApplicationRequest request) {
        User applicant = getCurrentUser();

        if (applicant.getRole() != Role.CUSTOMER) {
            throw new IllegalStateException("Only customers can apply to open a restaurant.");
        }

        applicationRepository.findByApplicantId(applicant.getId()).ifPresent(app -> {
            throw new IllegalStateException("You already have a pending or processed application.");
        });

        // Build the entity here in the service
        RestaurantApplication newApplication = new RestaurantApplication();
        newApplication.setRestaurantName(request.getRestaurantName());
        newApplication.setRestaurantAddress(request.getRestaurantAddress());
        newApplication.setRestaurantPhone(request.getRestaurantPhone());
        newApplication.setApplicant(applicant); // Set the logged-in user
        newApplication.setStatus(ApplicationStatus.PENDING);
        
        return applicationRepository.save(newApplication);
    }
    public List<RestaurantApplication> getPendingApplications() {
        return applicationRepository.findByStatus(ApplicationStatus.PENDING);
    }

   @Transactional
public Restaurant approveApplication(int applicationId) {
    RestaurantApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + applicationId));

    if (application.getStatus() != ApplicationStatus.PENDING) {
        throw new IllegalStateException("Application is not in a pending state.");
    }
    application.setStatus(ApplicationStatus.APPROVED);
    applicationRepository.save(application);

    User applicant = application.getApplicant();
    applicant.setRole(Role.RESTAURANT_OWNER);
    userRepository.save(applicant);

    Restaurant restaurant = new Restaurant();
    restaurant.setName(application.getRestaurantName());
    restaurant.setAddress(application.getRestaurantAddress());
    restaurant.setPhoneNumber(application.getRestaurantPhone());
    
    // You'll need to get this from the application object.
    // First, let's add the field to the application itself.
    // I'll assume you'll add 'locationPin' to the RestaurantApplication model and DTO
    // and set it in the 'applyForRestaurant' method.
    // For now, to solve the immediate error, let's use a placeholder.
    restaurant.setLocationPin("0.0,0.0"); // <<< TEMPORARY FIX

    restaurant.setOwner(applicant);
    return restaurantRepository.save(restaurant);
}

    // Add the reject method
    @Transactional
    public void rejectApplication(int applicationId, String reason) {
        RestaurantApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new IllegalStateException("Application is not in a pending state.");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setRejectionReason(reason);
        applicationRepository.save(application);
    }

    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}