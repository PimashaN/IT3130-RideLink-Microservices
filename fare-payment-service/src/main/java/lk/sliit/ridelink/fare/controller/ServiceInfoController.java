package lk.sliit.ridelink.fare.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class ServiceInfoController {

    private final int port;

    public ServiceInfoController(@Value("${server.port}") int port) {
        this.port = port;
    }

    @GetMapping("/")
    public Map<String, Object> serviceInfo() {
        Map<String, String> api = new LinkedHashMap<>();
        api.put("fares", "/api/fares");
        api.put("payments", "/api/payments");
        api.put("receipts", "/api/receipts");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service", "fare-payment-service");
        body.put("status", "UP");
        body.put("port", port);
        body.put("database", "MongoDB");
        body.put("api", api);
        return body;
    }
}
