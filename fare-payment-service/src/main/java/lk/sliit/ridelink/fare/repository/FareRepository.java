package lk.sliit.ridelink.fare.repository;

import lk.sliit.ridelink.fare.model.Fare;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface FareRepository extends MongoRepository<Fare, String> {

    Optional<Fare> findByRideId(Long rideId);

    List<Fare> findByPassengerId(String passengerId);

    List<Fare> findByDriverId(String driverId);

    boolean existsByRideId(Long rideId);
}
