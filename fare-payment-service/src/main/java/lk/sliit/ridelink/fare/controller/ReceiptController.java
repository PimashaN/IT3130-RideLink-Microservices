package lk.sliit.ridelink.fare.controller;

import lk.sliit.ridelink.fare.dto.ReceiptResponse;
import lk.sliit.ridelink.fare.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final PaymentService paymentService;

    public ReceiptController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{paymentId}")
    public ReceiptResponse getReceiptByPaymentId(
            @PathVariable String paymentId
    ) {
        return ReceiptResponse.from(paymentService.getReceiptByPaymentId(paymentId));
    }
}
