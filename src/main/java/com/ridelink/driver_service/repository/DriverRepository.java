package com.ridelink.driver_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ridelink.driver_service.model.Driver;

import java.util.List;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {

    // Search for available drivers
    List<Driver> findByAvailability(boolean availability);

    // Filter drivers by service area and availability (Eligible available drivers)
    List<Driver> findByServiceAreaAndAvailability(String serviceArea, boolean availability);
}