package lk.sliit.ridelink.account.dto;

import jakarta.validation.constraints.NotNull;
import lk.sliit.ridelink.account.model.Role;

public record UpdateRoleRequest(
        @NotNull(message = "Account role is required")
        Role role
) {
}
