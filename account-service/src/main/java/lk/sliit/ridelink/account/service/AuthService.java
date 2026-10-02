package lk.sliit.ridelink.account.service;

import lk.sliit.ridelink.account.dto.AuthResponse;
import lk.sliit.ridelink.account.dto.LoginRequest;
import lk.sliit.ridelink.account.security.AccountPrincipal;
import lk.sliit.ridelink.account.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.password()
                )
        );

        AccountPrincipal principal = (AccountPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                principal.getAccountId(),
                principal.getUsername(),
                principal.getRole()
        );
    }
}
