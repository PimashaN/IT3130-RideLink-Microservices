package lk.sliit.ridelink.account.dto;

import lk.sliit.ridelink.account.model.Role;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String accountId,
        String email,
        Role role
) {
}
