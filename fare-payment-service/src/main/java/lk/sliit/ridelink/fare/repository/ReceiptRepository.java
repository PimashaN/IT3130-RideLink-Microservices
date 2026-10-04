package lk.sliit.ridelink.fare.repository;

import lk.sliit.ridelink.fare.model.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReceiptRepository extends MongoRepository<Receipt, String> {

    Optional<Receipt> findByRideId(Long rideId);

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    Optional<Receipt> findByPaymentId(String paymentId);

    boolean existsByPaymentId(String paymentId);
}
