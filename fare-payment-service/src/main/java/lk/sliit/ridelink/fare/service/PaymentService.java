package lk.sliit.ridelink.fare.service;

import lk.sliit.ridelink.fare.exception.BadRequestException;
import lk.sliit.ridelink.fare.exception.ConflictException;
import lk.sliit.ridelink.fare.exception.ResourceNotFoundException;
import lk.sliit.ridelink.fare.model.Fare;
import lk.sliit.ridelink.fare.model.Payment;
import lk.sliit.ridelink.fare.model.PaymentMethod;
import lk.sliit.ridelink.fare.model.PaymentStatus;
import lk.sliit.ridelink.fare.model.Receipt;
import lk.sliit.ridelink.fare.repository.PaymentRepository;
import lk.sliit.ridelink.fare.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final int MONEY_SCALE = 2;
    private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_UP;

    private final FareService fareService;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;

    public PaymentService(
            FareService fareService,
            PaymentRepository paymentRepository,
            ReceiptRepository receiptRepository
    ) {
        this.fareService = fareService;
        this.paymentRepository = paymentRepository;
        this.receiptRepository = receiptRepository;
    }

    /**
     * Starts a simulated payment for an existing fare.
     * Ride status is not verified yet; the caller must only send a completed rideId.
     */
    public Payment initiatePayment(
            Long rideId,
            String payerAccountId,
            BigDecimal amount,
            PaymentMethod method
    ) {
        if (rideId == null || rideId <= 0) {
            throw new BadRequestException("rideId must be greater than 0");
        }
        if (payerAccountId == null || payerAccountId.isBlank()) {
            throw new BadRequestException("payerAccountId is required");
        }
        if (amount == null) {
            throw new BadRequestException("amount is required");
        }
        if (method == null) {
            throw new BadRequestException("payment method is required");
        }

        Fare fare = fareService.getFareByRideId(rideId);
        BigDecimal expectedAmount = fare.getTotalAmount().setScale(MONEY_SCALE, MONEY_ROUNDING);
        BigDecimal requestedAmount = amount.setScale(MONEY_SCALE, MONEY_ROUNDING);

        if (requestedAmount.compareTo(expectedAmount) != 0) {
            throw new BadRequestException(
                    "Payment amount must match the fare total of " + expectedAmount
            );
        }

        if (paymentRepository.existsByRideIdAndStatus(rideId, PaymentStatus.SUCCESS)) {
            throw new ConflictException("A successful payment already exists for rideId " + rideId);
        }
        if (paymentRepository.existsByRideIdAndStatus(rideId, PaymentStatus.PENDING)) {
            throw new ConflictException("A pending payment already exists for rideId " + rideId);
        }

        Payment payment = new Payment();
        payment.setFareId(fare.getId());
        payment.setRideId(rideId);
        payment.setPayerAccountId(payerAccountId.trim());
        payment.setAmount(requestedAmount);
        payment.setMethod(method);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().replace("-", ""));

        return paymentRepository.save(payment);
    }

    /**
     * Simulated capture: no real card/wallet gateway is called.
     * Cash, card, and wallet all move PENDING → SUCCESS, then a receipt is issued.
     */
    public Payment confirmPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new ConflictException("Payment " + paymentId + " is already successful");
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException(
                    "Only a PENDING payment can be confirmed. Current status: " + payment.getStatus()
            );
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(Instant.now());
        Payment saved = paymentRepository.save(payment);
        issueReceipt(saved);
        return saved;
    }

    public Payment failPayment(String paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new ConflictException("Payment " + paymentId + " is already failed");
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException(
                    "Only a PENDING payment can be marked as failed. Current status: " + payment.getStatus()
            );
        }

        payment.setStatus(PaymentStatus.FAILED);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new BadRequestException("paymentId is required");
        }

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id " + paymentId));
    }

    public List<Payment> getPaymentsByRideId(Long rideId) {
        if (rideId == null) {
            throw new BadRequestException("rideId is required");
        }
        return paymentRepository.findByRideId(rideId);
    }

    public Receipt getReceiptByPaymentId(String paymentId) {
        getPaymentById(paymentId);
        return receiptRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receipt not found for paymentId " + paymentId
                ));
    }

    private Receipt issueReceipt(Payment payment) {
        if (receiptRepository.existsByPaymentId(payment.getId())) {
            return receiptRepository.findByPaymentId(payment.getId()).orElseThrow();
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber("RCP-" + payment.getRideId() + "-" + payment.getId());
        receipt.setPaymentId(payment.getId());
        receipt.setRideId(payment.getRideId());
        receipt.setAmount(payment.getAmount());
        receipt.setMethod(payment.getMethod());
        receipt.setIssuedAt(Instant.now());
        return receiptRepository.save(receipt);
    }
}
