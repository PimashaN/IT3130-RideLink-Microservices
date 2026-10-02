package lk.sliit.ridelink.account.security;

import lk.sliit.ridelink.account.model.AccountStatus;
import lk.sliit.ridelink.account.model.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "VGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS10aGF0LWlzLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc=";

    @Test
    void shouldGenerateTokenWithAccountClaims() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);
        AccountPrincipal principal = new AccountPrincipal(
                "acc-123",
                "passenger@example.com",
                "encoded-password",
                Role.PASSENGER,
                AccountStatus.ACTIVE
        );

        String token = jwtService.generateToken(principal);

        assertThat(jwtService.extractAccountId(token)).isEqualTo("acc-123");
        assertThat(jwtService.extractEmail(token)).isEqualTo("passenger@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("PASSENGER");
        assertThat(jwtService.isTokenValid(token, principal)).isTrue();
    }

    @Test
    void shouldRejectTokenForDifferentAccount() {
        JwtService jwtService = new JwtService(TEST_SECRET, 3_600_000);
        AccountPrincipal tokenOwner = new AccountPrincipal(
                "acc-123",
                "passenger@example.com",
                "encoded-password",
                Role.PASSENGER,
                AccountStatus.ACTIVE
        );
        AccountPrincipal anotherAccount = new AccountPrincipal(
                "acc-999",
                "other@example.com",
                "encoded-password",
                Role.PASSENGER,
                AccountStatus.ACTIVE
        );

        String token = jwtService.generateToken(tokenOwner);

        assertThat(jwtService.isTokenValid(token, anotherAccount)).isFalse();
    }
}
