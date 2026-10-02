package lk.sliit.ridelink.account.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lk.sliit.ridelink.account.dto.RegisterRequest;
import lk.sliit.ridelink.account.model.Role;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestValidationTest {

    private final Validator validator = Validation
            .buildDefaultValidatorFactory()
            .getValidator();

    @Test
    void shouldRejectInvalidRegistrationFields() {
        RegisterRequest request = new RegisterRequest(
                "A",
                "invalid-email",
                "short",
                "12",
                Role.PASSENGER
        );

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("fullName", "email", "password", "phoneNumber");
    }
}
