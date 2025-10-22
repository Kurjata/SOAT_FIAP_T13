package soat_fiap.siaes.interfaces.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.user.model.RoleEnum;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Vinicius",
                "384.088.920-02",
                RoleEnum.CLIENT
        );

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_name_is_blank() {
        UpdateUserRequest request = new UpdateUserRequest(
                "",
                "384.088.920-02",
                RoleEnum.CLIENT
        );

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("must not be blank");
    }

    @Test
    void should_fail_when_document_is_blank() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Vinicius",
                "",
                RoleEnum.CLIENT
        );

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("must not be blank");
    }

    @Test
    void should_fail_when_role_is_null() {
        UpdateUserRequest request = new UpdateUserRequest(
                "Vinicius",
                "384.088.920-02",
                null
        );

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("must not be null");
    }

    @Test
    void should_fail_when_all_fields_are_invalid() {
        UpdateUserRequest request = new UpdateUserRequest(
                "",
                "",
                null
        );

        Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("must not be blank", "must not be null");
    }
}