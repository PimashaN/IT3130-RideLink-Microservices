package com.ridelink.driver_service.repository;

import com.ridelink.driver_service.model.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    
    // Search for a vehicle by driver ID
    Optional<Vehicle> findByDriverId(String driverId);
}