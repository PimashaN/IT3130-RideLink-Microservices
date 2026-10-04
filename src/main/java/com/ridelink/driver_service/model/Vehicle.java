package com.ridelink.driver_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehicles")
public class Vehicle {

    @Id
    private String id;
    private String driverId;      // driver's id in vehicle
    private String vehicleNumber; // vehicle number (e.g., WP-ABC-1234)
    private String vehicleType;   // type (Car, Van, Bike, Three-Wheel)
    private String model;         // model (Alto, Prius, etc.)
    private int seatingCapacity;  // seating capacity

    // Constructors
    public Vehicle() {}

    public Vehicle(String id, String driverId, String vehicleNumber, String vehicleType, String model, int seatingCapacity) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.model = model;
        this.seatingCapacity = seatingCapacity;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(int seatingCapacity) { this.seatingCapacity = seatingCapacity; }
}