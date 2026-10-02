package lk.sliit.ridelink.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.sliit.ridelink.account.dto.AccountResponse;
import lk.sliit.ridelink.account.dto.AuthResponse;
import lk.sliit.ridelink.account.dto.LoginRequest;
import lk.sliit.ridelink.account.dto.RegisterRequest;
import lk.sliit.ridelink.account.service.AccountService;
import lk.sliit.ridelink.account.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Public account registration and login")
public class AuthController {

    private final AccountService accountService;
    private final AuthService authService;

    public AuthController(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a passenger or driver account")
    @ApiResponse(responseCode = "201", description = "Account created")
    @ApiResponse(responseCode = "400", description = "Invalid request or ADMIN role requested")
    @ApiResponse(responseCode = "409", description = "Email already registered")
    public ResponseEntity<AccountResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive a JWT access token")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials or disabled account")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
