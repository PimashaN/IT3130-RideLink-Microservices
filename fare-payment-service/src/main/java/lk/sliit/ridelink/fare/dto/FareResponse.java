package lk.sliit.ridelink.fare.dto;

import lk.sliit.ridelink.fare.model.Fare;

import java.math.BigDecimal;
import java.time.Instant;

public record FareResponse(
        String id,
        Long rideId,
        String passengerId,
        String driverId,
        BigDecimal distanceKm,
        Integer durationMinutes,
        BigDecimal baseFare,
        BigDecimal perKmRate,
        BigDecimal perMinuteRate,
        BigDecimal surgeMultiplier,
        BigDecimal totalAmount,
        String currency,
        Instant createdAt
) {
    public static FareResponse from(Fare fare) {
        return new FareResponse(
                fare.getId(),
                fare.getRideId(),
                fare.getPassengerId(),
                fare.getDriverId(),
                fare.getDistanceKm(),
                fare.getDurationMinutes(),
                fare.getBaseFare(),
                fare.getPerKmRate(),
                fare.getPerMinuteRate(),
                fare.getSurgeMultiplier(),
                fare.getTotalAmount(),
                fare.getCurrency(),
                fare.getCreatedAt()
        );
    }
}
