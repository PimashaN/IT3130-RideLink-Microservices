package com.ridelink.driver_service.service;

import com.ridelink.driver_service.model.Driver;
import com.ridelink.driver_service.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    // 1. Get all drivers
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // 2. Get driver by ID
    public Optional<Driver> getDriverById(String id) {
        return driverRepository.findById(id);
    }

    // 3. Save a new driver
    public Driver saveDriver(Driver driver) {
        return driverRepository.save(driver);
    }

    // 4. Update driver information
    public Driver updateDriver(String id, Driver driverDetails) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setName(driverDetails.getName());
            driver.setPhone(driverDetails.getPhone());
            driver.setAvailability(driverDetails.isAvailability());
            driver.setServiceArea(driverDetails.getServiceArea());
            driver.setLatitude(driverDetails.getLatitude());
            driver.setLongitude(driverDetails.getLongitude());
            return driverRepository.save(driver);
        }
        throw new RuntimeException("Driver not found with id: " + id);
    }

    // 5. Update availability
    public Driver updateAvailability(String id, boolean availability) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setAvailability(availability);
            return driverRepository.save(driver);
        }
        throw new RuntimeException("Driver not found with id: " + id);
    }

    // 6. Update location
    public Driver updateLocation(String id, double latitude, double longitude) {
        Optional<Driver> optionalDriver = driverRepository.findById(id);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setLatitude(latitude);
            driver.setLongitude(longitude);
            return driverRepository.save(driver);
        }
        throw new RuntimeException("Driver not found with id: " + id);
    }

    // 7. Search drivers by service area and availability
    public List<Driver> searchDrivers(String serviceArea, boolean availability) {
        return driverRepository.findByServiceAreaAndAvailability(serviceArea, availability);
    }

    // 8. Delete a driver
    public boolean deleteDriver(String id) {
        if (driverRepository.existsById(id)) {
            driverRepository.deleteById(id);
            return true;
        }
        return false;
    }
}