package com.example.ride_service.service;

import com.example.ride_service.model.Ride;
import com.example.ride_service.repository.RideRepository;
import com.example.ride_service.exception.RideNotFoundException;
import org.springframework.stereotype.Service;
import com.example.ride_service.model.RideStatus;
import com.example.ride_service.client.DriverServiceClient;
import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;

    public RideService(
        RideRepository rideRepository,
        DriverServiceClient driverServiceClient) {

    this.rideRepository = rideRepository;
    this.driverServiceClient = driverServiceClient;
    }

    public Ride createRide(Ride ride) {

    DriverServiceClient.Driver driver =
            driverServiceClient.getAvailableDriver();

    if (driver == null) {
        throw new IllegalStateException("No available driver found");
    }

    ride.setDriverId(driver.getId());

        driverServiceClient.updateDriverAvailability(
        driver.getId(),
        false
    );

    ride.setStatus(RideStatus.REQUESTED);

    return rideRepository.save(ride);
    }

    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
    }
    public Ride acceptRide(Long id) {
    Ride ride = getRideById(id);

    if (ride.getStatus() != RideStatus.REQUESTED) {
        throw new IllegalStateException("Ride cannot be accepted in current status");
    }

    ride.setStatus(RideStatus.ACCEPTED);

    return rideRepository.save(ride);
    }
    public Ride startRide(Long id) {
    Ride ride = getRideById(id);

    if (ride.getStatus() != RideStatus.ACCEPTED) {
        throw new IllegalStateException("Ride can only be started after acceptance");
    }

    ride.setStatus(RideStatus.STARTED);

    return rideRepository.save(ride);
    }
    public Ride completeRide(Long id) {
    Ride ride = getRideById(id);

    if (ride.getStatus() != RideStatus.STARTED) {
        throw new IllegalStateException("Ride can only be completed after it has started");
    }

    ride.setStatus(RideStatus.COMPLETED);

    return rideRepository.save(ride);
    }
    public Ride cancelRide(Long id) {
    Ride ride = getRideById(id);

    if (ride.getStatus() != RideStatus.REQUESTED &&
            ride.getStatus() != RideStatus.ACCEPTED) {
        throw new IllegalStateException(
                "Ride can only be cancelled when it is requested or accepted"
        );
    }

    ride.setStatus(RideStatus.CANCELLED);

    return rideRepository.save(ride);
    }
}