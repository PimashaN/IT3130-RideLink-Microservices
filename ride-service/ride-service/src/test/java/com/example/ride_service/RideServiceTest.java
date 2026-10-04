package com.example.ride_service;

import com.example.ride_service.client.DriverServiceClient;
import com.example.ride_service.model.Ride;
import com.example.ride_service.model.RideStatus;
import com.example.ride_service.repository.RideRepository;
import com.example.ride_service.service.RideService;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RideServiceTest {

    @Test
void createRide_shouldSetStatusToRequested() {

    RideRepository rideRepository = mock(RideRepository.class);
    DriverServiceClient driverServiceClient = mock(DriverServiceClient.class);

    RideService rideService =
            new RideService(rideRepository, driverServiceClient);

    DriverServiceClient.Driver driver =
            new DriverServiceClient.Driver();

    driver.setId(101L);
    driver.setName("Nimal Perera");
    driver.setPhone("0712345678");

    when(driverServiceClient.getAvailableDriver())
            .thenReturn(driver);

    Ride ride = new Ride();
    ride.setPassengerName("Ayesha");
    ride.setPickup("Anuradhapura");
    ride.setDestination("Kandy");

    when(rideRepository.save(any(Ride.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    Ride result = rideService.createRide(ride);

    assertEquals(RideStatus.REQUESTED, result.getStatus());
    assertEquals(101L, result.getDriverId());

    verify(driverServiceClient).getAvailableDriver();
    verify(rideRepository).save(ride);
    }

    @Test
    void acceptRide_shouldChangeStatusToAccepted() {

        RideRepository rideRepository = mock(RideRepository.class);
        DriverServiceClient driverServiceClient = mock(DriverServiceClient.class);

        RideService rideService =
                new RideService(rideRepository, driverServiceClient);

        Ride ride = new Ride();
        ride.setStatus(RideStatus.REQUESTED);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride result = rideService.acceptRide(1L);

        assertEquals(RideStatus.ACCEPTED, result.getStatus());
        verify(rideRepository).save(ride);
    }

    @Test
    void startRide_shouldChangeStatusToStarted() {

        RideRepository rideRepository = mock(RideRepository.class);
        DriverServiceClient driverServiceClient = mock(DriverServiceClient.class);

        RideService rideService =
                new RideService(rideRepository, driverServiceClient);

        Ride ride = new Ride();
        ride.setStatus(RideStatus.ACCEPTED);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride result = rideService.startRide(1L);

        assertEquals(RideStatus.STARTED, result.getStatus());
        verify(rideRepository).save(ride);
    }

    @Test
    void completeRide_shouldChangeStatusToCompleted() {

        RideRepository rideRepository = mock(RideRepository.class);
        DriverServiceClient driverServiceClient = mock(DriverServiceClient.class);

        RideService rideService =
                new RideService(rideRepository, driverServiceClient);

        Ride ride = new Ride();
        ride.setStatus(RideStatus.STARTED);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride result = rideService.completeRide(1L);

        assertEquals(RideStatus.COMPLETED, result.getStatus());
        verify(rideRepository).save(ride);
    }

    @Test
    void cancelRide_shouldChangeStatusToCancelled() {

        RideRepository rideRepository = mock(RideRepository.class);
        DriverServiceClient driverServiceClient = mock(DriverServiceClient.class);

        RideService rideService =
                new RideService(rideRepository, driverServiceClient);

        Ride ride = new Ride();
        ride.setStatus(RideStatus.REQUESTED);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);

        Ride result = rideService.cancelRide(1L);

        assertEquals(RideStatus.CANCELLED, result.getStatus());
        verify(rideRepository).save(ride);
    }
}