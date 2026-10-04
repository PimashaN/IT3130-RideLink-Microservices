package com.example.ride_service.controller;

import com.example.ride_service.model.Ride;
import com.example.ride_service.service.RideService;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public Ride createRide(@Valid @RequestBody Ride ride) {
        return rideService.createRide(ride);
    }

    @GetMapping
    public List<Ride> getAllRides() {
        return rideService.getAllRides();
    }

    @GetMapping("/{id}")
    public Ride getRideById(@PathVariable Long id) {
        return rideService.getRideById(id);
    }
    @PutMapping("/{id}/accept")
     public Ride acceptRide(@PathVariable Long id) {
        return rideService.acceptRide(id);
    }
    @PutMapping("/{id}/start")
    public Ride startRide(@PathVariable Long id) {
    return rideService.startRide(id);
    }

    @PutMapping("/{id}/complete")
    public Ride completeRide(@PathVariable Long id) {
    return rideService.completeRide(id);
    }

    @PutMapping("/{id}/cancel")
    public Ride cancelRide(@PathVariable Long id) {
    return rideService.cancelRide(id);
    }
}