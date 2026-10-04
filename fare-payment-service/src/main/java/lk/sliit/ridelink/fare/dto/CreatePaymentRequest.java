package lk.sliit.ridelink.fare.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lk.sliit.ridelink.fare.model.PaymentMethod;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull(message = "rideId is required")
        @Positive(message = "rideId must be greater than 0")
        Long rideId,

        @NotBlank(message = "payerAccountId is required")
        String payerAccountId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "payment method is required")
        PaymentMethod method
) {
}
