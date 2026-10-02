package lk.sliit.ridelink.account.service;

import lk.sliit.ridelink.account.dto.AuthResponse;
import lk.sliit.ridelink.account.dto.LoginRequest;
import lk.sliit.ridelink.account.model.AccountStatus;
import lk.sliit.ridelink.account.model.Role;
import lk.sliit.ridelink.account.security.AccountPrincipal;
import lk.sliit.ridelink.account.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(authenticationManager, jwtService);
    }

    @Test
    void shouldAuthenticateAndReturnJwt() {
        AccountPrincipal principal = new AccountPrincipal(
                "acc-001",
                "driver@example.com",
                "encoded-password",
                Role.DRIVER,
                AccountStatus.ACTIVE
        );
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(principal);
        when(jwtService.generateToken(principal)).thenReturn("signed.jwt.token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.login(
                new LoginRequest("DRIVER@EXAMPLE.COM", "Password@123")
        );

        assertThat(response.accessToken()).isEqualTo("signed.jwt.token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accountId()).isEqualTo("acc-001");
        assertThat(response.email()).isEqualTo("driver@example.com");
        assertThat(response.role()).isEqualTo(Role.DRIVER);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        "driver@example.com",
                        "Password@123"
                )
        );
    }
}
