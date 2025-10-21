package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UpdateSupplyRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "Graxa Industrial", new BigDecimal("89.90"),
                UnitMeasure.LT, "Shell", true
        );

        Set<ConstraintViolation<UpdateSupplyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_name_is_blank() {
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "", new BigDecimal("89.90"),
                UnitMeasure.LT, "Shell", true
        );

        Set<ConstraintViolation<UpdateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be blank");
    }

    @Test
    void should_fail_when_unit_price_is_null_or_not_positive() {
        UpdateSupplyRequest nullPrice = new UpdateSupplyRequest(
                "Graxa Industrial", null,
                UnitMeasure.LT, "Shell", true
        );
        UpdateSupplyRequest zeroPrice = new UpdateSupplyRequest(
                "Graxa Industrial", BigDecimal.ZERO,
                UnitMeasure.LT, "Shell", true
        );

        assertThat(validator.validate(nullPrice))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");

        assertThat(validator.validate(zeroPrice))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must be greater than 0");
    }

    @Test
    void should_fail_when_unit_measure_is_null() {
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "Graxa Industrial", new BigDecimal("89.90"),
                null, "Shell", true
        );

        Set<ConstraintViolation<UpdateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void should_fail_when_supplier_is_blank() {
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "Graxa Industrial", new BigDecimal("89.90"),
                UnitMeasure.LT, "", true
        );

        Set<ConstraintViolation<UpdateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be blank");
    }

    @Test
    void should_fail_when_available_is_null() {
        UpdateSupplyRequest request = new UpdateSupplyRequest(
                "Graxa Industrial", new BigDecimal("89.90"),
                UnitMeasure.LT, "Shell", null
        );

        Set<ConstraintViolation<UpdateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }
}