package lk.sliit.ridelink.account.dto;

import lk.sliit.ridelink.account.model.AccountStatus;
import lk.sliit.ridelink.account.model.Role;

import java.time.Instant;

public record AccountResponse(
        String id,
        String fullName,
        String email,
        String phoneNumber,
        Role role,
        AccountStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
