package lk.sliit.ridelink.fare.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lk.sliit.ridelink.fare.dto.CreatePaymentRequest;
import lk.sliit.ridelink.fare.dto.PaymentResponse;
import lk.sliit.ridelink.fare.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentResponse body = PaymentResponse.from(paymentService.initiatePayment(
                request.rideId(),
                request.payerAccountId(),
                request.amount(),
                request.method()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/{id}/confirm")
    public PaymentResponse confirmPayment(
            @PathVariable String id
    ) {
        return PaymentResponse.from(paymentService.confirmPayment(id));
    }

    @PostMapping("/{id}/fail")
    public PaymentResponse failPayment(
            @PathVariable String id
    ) {
        return PaymentResponse.from(paymentService.failPayment(id));
    }

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(
            @PathVariable String id
    ) {
        return PaymentResponse.from(paymentService.getPaymentById(id));
    }

    @GetMapping("/ride/{rideId}")
    public List<PaymentResponse> getPaymentsByRideId(
            @PathVariable @Positive(message = "rideId must be greater than 0") Long rideId
    ) {
        return paymentService.getPaymentsByRideId(rideId).stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
