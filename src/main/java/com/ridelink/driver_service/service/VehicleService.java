package com.ridelink.driver_service.service;

import com.ridelink.driver_service.model.Vehicle;
import com.ridelink.driver_service.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    // 1. Register / Save a vehicle
    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    // 2. Get all vehicles
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    // 3. Get a vehicle by ID
    public Optional<Vehicle> getVehicleById(String id) {
        return vehicleRepository.findById(id);
    }

    // 4. Get a vehicle by Driver ID
    public Optional<Vehicle> getVehicleByDriverId(String driverId) {
        return vehicleRepository.findByDriverId(driverId);
    }

    // 5. Update vehicle information
    public Vehicle updateVehicle(String id, Vehicle vehicleDetails) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setVehicleNumber(vehicleDetails.getVehicleNumber());
            vehicle.setVehicleType(vehicleDetails.getVehicleType());
            vehicle.setModel(vehicleDetails.getModel());
            vehicle.setSeatingCapacity(vehicleDetails.getSeatingCapacity());
            return vehicleRepository.save(vehicle);
        }
        throw new RuntimeException("Vehicle not found with id: " + id);
    }
}