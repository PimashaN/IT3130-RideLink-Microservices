package com.example.ride_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DriverServiceClient {

    private final RestClient restClient;

    public DriverServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${driver.service.base-url}") String driverServiceBaseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(driverServiceBaseUrl)
                .build();
    }

    public Driver getAvailableDriver() {

        return restClient.get()
                .uri("/api/drivers/available")
                .retrieve()
                .body(Driver.class);
    }

    public void updateDriverAvailability(Long driverId, boolean available) {

        restClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/drivers/{id}/availability")
                        .queryParam("available", available)
                        .build(driverId))
                .retrieve()
                .toBodilessEntity();
    }

    public static class Driver {

        private Long id;
        private String name;
        private String phone;
        private String service_area;
        private boolean availability;
        private double latitude;
        private double longitude;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getService_area() {
            return service_area;
        }

        public void setService_area(String service_area) {
            this.service_area = service_area;
        }

        public boolean isAvailability() {
            return availability;
        }

        public void setAvailability(boolean availability) {
            this.availability = availability;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}