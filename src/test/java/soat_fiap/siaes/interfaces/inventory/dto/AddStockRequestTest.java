package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AddStockRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        AddStockRequest request = new AddStockRequest(10);
        Set<ConstraintViolation<AddStockRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_quantity_is_null() {
        AddStockRequest request = new AddStockRequest(null);
        Set<ConstraintViolation<AddStockRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void should_fail_when_quantity_is_not_positive() {
        AddStockRequest zeroQuantity = new AddStockRequest(0);
        AddStockRequest negativeQuantity = new AddStockRequest(-5);

        Set<ConstraintViolation<AddStockRequest>> zeroViolations = validator.validate(zeroQuantity);
        Set<ConstraintViolation<AddStockRequest>> negativeViolations = validator.validate(negativeQuantity);

        assertThat(zeroViolations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must be greater than 0");

        assertThat(negativeViolations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must be greater than 0");
    }


}