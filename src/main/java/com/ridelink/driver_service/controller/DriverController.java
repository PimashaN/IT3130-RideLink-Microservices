package com.ridelink.driver_service.controller;

import com.ridelink.driver_service.model.Driver;
import com.ridelink.driver_service.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    // 1. Get the list of all drivers (GET)
    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    // 2. Get the driver by ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable String id) {
        Optional<Driver> driver = driverService.getDriverById(id);
        return driver.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. Create a new driver (POST)
    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) {
        Driver savedDriver = driverService.saveDriver(driver);
        return ResponseEntity.status(201).body(savedDriver);
    }

    // 4. Update driver information (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable String id, @Valid @RequestBody Driver driverDetails) {
        try {
            Driver updatedDriver = driverService.updateDriver(id, driverDetails);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Update driver's availability (PATCH)
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Driver> updateAvailability(@PathVariable String id, @RequestParam boolean availability) {
        try {
            Driver updatedDriver = driverService.updateAvailability(id, availability);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. Update driver's GPS location (PATCH)
    @PatchMapping("/{id}/location")
    public ResponseEntity<Driver> updateLocation(@PathVariable String id, @RequestParam double latitude, @RequestParam double longitude) {
        try {
            Driver updatedDriver = driverService.updateLocation(id, latitude, longitude);
            return ResponseEntity.ok(updatedDriver);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 7. Search drivers by service area and availability (GET) - For general use & Ride Service
    @GetMapping("/search")
    public List<Driver> searchDrivers(@RequestParam String serviceArea, @RequestParam boolean availability) {
        return driverService.searchDrivers(serviceArea, availability);
    }

    // 8. Specific endpoint for Ride Service to get available drivers in an area
    @GetMapping("/available")
    public ResponseEntity<List<Driver>> getAvailableDrivers(@RequestParam String serviceArea) {
        List<Driver> drivers = driverService.searchDrivers(serviceArea, true);
        return ResponseEntity.ok(drivers);
    }

    // 9. Delete a driver (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String id) {
        boolean deleted = driverService.deleteDriver(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}