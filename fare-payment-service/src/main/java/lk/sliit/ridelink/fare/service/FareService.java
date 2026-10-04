package lk.sliit.ridelink.fare.service;

import lk.sliit.ridelink.fare.exception.BadRequestException;
import lk.sliit.ridelink.fare.exception.ConflictException;
import lk.sliit.ridelink.fare.exception.ResourceNotFoundException;
import lk.sliit.ridelink.fare.model.Fare;
import lk.sliit.ridelink.fare.repository.FareRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FareService {

    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    private final FareRepository fareRepository;
    private final BigDecimal baseFare;
    private final BigDecimal perKmRate;
    private final BigDecimal perMinuteRate;
    private final BigDecimal surgeMultiplier;
    private final String currency;

    public FareService(
            FareRepository fareRepository,
            @Value("${fare.base}") BigDecimal baseFare,
            @Value("${fare.per-km}") BigDecimal perKmRate,
            @Value("${fare.per-minute}") BigDecimal perMinuteRate,
            @Value("${fare.surge-multiplier}") BigDecimal surgeMultiplier,
            @Value("${fare.currency}") String currency
    ) {
        this.fareRepository = fareRepository;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.perMinuteRate = perMinuteRate;
        this.surgeMultiplier = surgeMultiplier;
        this.currency = currency;
    }

    /**
     * Calculates and stores a fare for a completed ride.
     * Ride status is not verified yet; the caller must only send a completed rideId.
     */
    public Fare calculateFare(
            Long rideId,
            String passengerId,
            String driverId,
            BigDecimal distanceKm,
            Integer durationMinutes
    ) {
        validateCalculateRequest(rideId, passengerId, driverId, distanceKm, durationMinutes);

        if (fareRepository.existsByRideId(rideId)) {
            throw new ConflictException("A fare already exists for rideId " + rideId);
        }

        Fare fare = new Fare();
        fare.setRideId(rideId);
        fare.setPassengerId(passengerId.trim());
        fare.setDriverId(driverId.trim());
        fare.setDistanceKm(distanceKm.setScale(MONEY_SCALE, MONEY_ROUNDING));
        fare.setDurationMinutes(durationMinutes);
        fare.setBaseFare(scale(baseFare));
        fare.setPerKmRate(scale(perKmRate));
        fare.setPerMinuteRate(scale(perMinuteRate));
        fare.setSurgeMultiplier(surgeMultiplier);
        fare.setCurrency(currency);
        fare.setTotalAmount(computeTotal(distanceKm, durationMinutes));

        return fareRepository.save(fare);
    }

    public Fare getFareById(String fareId) {
        if (fareId == null || fareId.isBlank()) {
            throw new BadRequestException("fareId is required");
        }

        return fareRepository.findById(fareId)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found with id " + fareId));
    }

    public Fare getFareByRideId(Long rideId) {
        if (rideId == null) {
            throw new BadRequestException("rideId is required");
        }

        return fareRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Fare not found for rideId " + rideId));
    }

    private BigDecimal computeTotal(BigDecimal distanceKm, Integer durationMinutes) {
        BigDecimal distanceCharge = distanceKm.multiply(perKmRate);
        BigDecimal timeCharge = perMinuteRate.multiply(BigDecimal.valueOf(durationMinutes));
        BigDecimal subtotal = baseFare.add(distanceCharge).add(timeCharge);
        return scale(subtotal.multiply(surgeMultiplier));
    }

    private void validateCalculateRequest(
            Long rideId,
            String passengerId,
            String driverId,
            BigDecimal distanceKm,
            Integer durationMinutes
    ) {
        if (rideId == null || rideId <= 0) {
            throw new BadRequestException("rideId must be greater than 0");
        }
        if (passengerId == null || passengerId.isBlank()) {
            throw new BadRequestException("passengerId is required");
        }
        if (driverId == null || driverId.isBlank()) {
            throw new BadRequestException("driverId is required");
        }
        if (distanceKm == null || distanceKm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("distanceKm must be greater than 0");
        }
        if (durationMinutes == null || durationMinutes <= 0) {
            throw new BadRequestException("durationMinutes must be greater than 0");
        }
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(MONEY_SCALE, MONEY_ROUNDING);
    }
}
