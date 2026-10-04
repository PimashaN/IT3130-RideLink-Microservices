package lk.sliit.ridelink.fare.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CalculateFareRequest(
        @NotNull(message = "rideId is required")
        @Positive(message = "rideId must be greater than 0")
        Long rideId,

        @NotBlank(message = "passengerId is required")
        String passengerId,

        @NotBlank(message = "driverId is required")
        String driverId,

        @NotNull(message = "distanceKm is required")
        @DecimalMin(value = "0.01", message = "distanceKm must be greater than 0")
        BigDecimal distanceKm,

        @NotNull(message = "durationMinutes is required")
        @Positive(message = "durationMinutes must be greater than 0")
        Integer durationMinutes
) {
}
