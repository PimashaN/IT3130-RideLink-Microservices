package lk.sliit.ridelink.account.dto;

import jakarta.validation.constraints.NotNull;
import lk.sliit.ridelink.account.model.AccountStatus;

public record UpdateStatusRequest(
        @NotNull(message = "Account status is required")
        AccountStatus status
) {
}
