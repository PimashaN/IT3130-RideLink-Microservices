package lk.sliit.ridelink.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lk.sliit.ridelink.account.model.Role;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must contain 2 to 100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must contain 8 to 72 characters")
        String password,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9+][0-9 -]{8,14}$", message = "Phone number must be valid")
        String phoneNumber,

        @NotNull(message = "Role is required")
        Role role
) {
}
