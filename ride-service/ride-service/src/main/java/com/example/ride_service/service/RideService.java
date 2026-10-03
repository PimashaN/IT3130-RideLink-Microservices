package com.example.ride_service.service;

import com.example.ride_service.exception.RideNotFoundException;
import com.example.ride_service.model.Ride;
import com.example.ride_service.model.RideStatus;
import com.example.ride_service.repository.RideRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    // Create ride request
    public Ride createRide(Ride ride) {

        ride.setStatus(RideStatus.REQUESTED);

        return rideRepository.save(ride);
    }

    // Get all rides
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    // Get ride by ID
    public Ride getRideById(String id) {

        return rideRepository.findById(id)
                .orElseThrow(() ->
                        new RideNotFoundException(
                                "Ride not found with id: " + id
                        ));
    }

    // Assign driver
    public Ride assignDriver(String id, Long driverId) {

        Ride ride = getRideById(id);

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new IllegalStateException(
                    "Driver can only be assigned to a requested ride"
            );
        }

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);

        return rideRepository.save(ride);
    }

    // Accept ride
    public Ride acceptRide(String id) {

        Ride ride = getRideById(id);

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Ride can only be accepted after driver assignment"
            );
        }

        ride.setStatus(RideStatus.ACCEPTED);

        return rideRepository.save(ride);
    }

    // Start ride
    public Ride startRide(String id) {

        Ride ride = getRideById(id);

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Ride can only be started after acceptance"
            );
        }

        ride.setStatus(RideStatus.IN_PROGRESS);

        return rideRepository.save(ride);
    }

    // Complete ride
    public Ride completeRide(String id) {

        Ride ride = getRideById(id);

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Ride can only be completed when it is in progress"
            );
        }

        ride.setStatus(RideStatus.COMPLETED);

        return rideRepository.save(ride);
    }

    // Cancel ride
    public Ride cancelRide(String id) {

        Ride ride = getRideById(id);

        if (ride.getStatus() != RideStatus.REQUESTED &&
                ride.getStatus() != RideStatus.ASSIGNED &&
                ride.getStatus() != RideStatus.ACCEPTED) {

            throw new IllegalStateException(
                    "Ride can only be cancelled when requested, assigned or accepted"
            );
        }

        ride.setStatus(RideStatus.CANCELLED);

        return rideRepository.save(ride);
    }
}