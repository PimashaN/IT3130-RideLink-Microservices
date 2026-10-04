package lk.sliit.ridelink.fare.dto;

import lk.sliit.ridelink.fare.model.PaymentMethod;
import lk.sliit.ridelink.fare.model.Receipt;

import java.math.BigDecimal;
import java.time.Instant;

public record ReceiptResponse(
        String id,
        String receiptNumber,
        String paymentId,
        Long rideId,
        BigDecimal amount,
        PaymentMethod method,
        Instant issuedAt
) {
    public static ReceiptResponse from(Receipt receipt) {
        String paymentId = receipt.getPaymentId();
        return new ReceiptResponse(
                receipt.getId(),
                receipt.getReceiptNumber(),
                paymentId,
                receipt.getRideId(),
                receipt.getAmount(),
                receipt.getMethod(),
                receipt.getIssuedAt()
        );
    }
}
