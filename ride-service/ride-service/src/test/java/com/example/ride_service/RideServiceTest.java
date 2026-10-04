
package com.example.ride_service;

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

        RideService rideService =
                new RideService(rideRepository);

        Ride ride = new Ride();

        ride.setPassengerName("Ayesha");
        ride.setPickup("Anuradhapura");
        ride.setDestination("Kandy");

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Ride result = rideService.createRide(ride);

        assertEquals(RideStatus.REQUESTED, result.getStatus());

        verify(rideRepository).save(ride);
    }


    @Test
    void assignDriver_shouldSetDriverAndStatusToAssigned() {

        RideRepository rideRepository = mock(RideRepository.class);

        RideService rideService =
                new RideService(rideRepository);

        Ride ride = new Ride();

        ride.setStatus(RideStatus.REQUESTED);

        when(rideRepository.findById("1"))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(ride))
                .thenReturn(ride);

        Ride result = rideService.assignDriver("1", 101L);

        assertEquals(101L, result.getDriverId());
        assertEquals(RideStatus.ASSIGNED, result.getStatus());

        verify(rideRepository).save(ride);
    }


    @Test
    void acceptRide_shouldChangeStatusToAccepted() {

        RideRepository rideRepository = mock(RideRepository.class);

        RideService rideService =
                new RideService(rideRepository);

        Ride ride = new Ride();

        ride.setStatus(RideStatus.ASSIGNED);
        ride.setDriverId(101L);

        when(rideRepository.findById("1"))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(ride))
                .thenReturn(ride);

        Ride result = rideService.acceptRide("1");

        assertEquals(RideStatus.ACCEPTED, result.getStatus());

        verify(rideRepository).save(ride);
    }


    @Test
    void startRide_shouldChangeStatusToInProgress() {

        RideRepository rideRepository = mock(RideRepository.class);

        RideService rideService =
                new RideService(rideRepository);

        Ride ride = new Ride();

        ride.setStatus(RideStatus.ACCEPTED);

        when(rideRepository.findById("1"))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(ride))
                .thenReturn(ride);

        Ride result = rideService.startRide("1");

        assertEquals(RideStatus.IN_PROGRESS, result.getStatus());

        verify(rideRepository).save(ride);
    }


    @Test
    void completeRide_shouldChangeStatusToCompleted() {

        RideRepository rideRepository = mock(RideRepository.class);

        RideService rideService =
                new RideService(rideRepository);

        Ride ride = new Ride();

        ride.setStatus(RideStatus.IN_PROGRESS);

        when(rideRepository.findById("1"))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(ride))
                .thenReturn(ride);

        Ride result = rideService.completeRide("1");

        assertEquals(RideStatus.COMPLETED, result.getStatus());

        verify(rideRepository).save(ride);
    }


    @Test
    void cancelRide_shouldChangeStatusToCancelled() {

        RideRepository rideRepository = mock(RideRepository.class);

        RideService rideService =
                new RideService(rideRepository);

        Ride ride = new Ride();

        ride.setStatus(RideStatus.REQUESTED);

        when(rideRepository.findById("1"))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(ride))
                .thenReturn(ride);

        Ride result = rideService.cancelRide("1");

        assertEquals(RideStatus.CANCELLED, result.getStatus());

        verify(rideRepository).save(ride);
    }
}