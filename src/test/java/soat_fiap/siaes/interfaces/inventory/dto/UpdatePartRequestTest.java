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

class UpdatePartRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        UpdatePartRequest request = new UpdatePartRequest("Filtro de óleo", new BigDecimal("25.50"), UnitMeasure.UNIT,
                10, 2, 5, "1234567890123", "Top"
        );

        Set<ConstraintViolation<UpdatePartRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_name_is_blank() {
        UpdatePartRequest request = new UpdatePartRequest("", new BigDecimal("25.50"), UnitMeasure.UNIT,
                10, 2, 5, "1234567890123", "top");

        Set<ConstraintViolation<UpdatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve estar em branco");
    }

    @Test
    void should_fail_when_unit_price_is_null_or_not_positive() {
        UpdatePartRequest nullPrice = new UpdatePartRequest("Filtro de óleo", null, UnitMeasure.UNIT,
                10, 2, 5, "1234567890123", "top");

        UpdatePartRequest zeroPrice = new UpdatePartRequest("Filtro de óleo", BigDecimal.ZERO, UnitMeasure.UNIT,
                10, 2, 5, "1234567890123", "top");

        Set<ConstraintViolation<UpdatePartRequest>> nullViolations = validator.validate(nullPrice);
        Set<ConstraintViolation<UpdatePartRequest>> zeroViolations = validator.validate(zeroPrice);

        assertThat(nullViolations)
                .extracting(ConstraintViolation::getMessage)
                .contains("não deve ser nulo");

        assertThat(zeroViolations)
                .extracting(ConstraintViolation::getMessage)
                .contains("deve ser maior que 0");
    }

    @Test
    void should_fail_when_quantity_is_null_or_not_positive() {
        UpdatePartRequest nullQuantity = new UpdatePartRequest("Filtro de óleo", new BigDecimal("25.50"),
                UnitMeasure.UNIT, null, 2, 5, "1234567890123", "top");

        UpdatePartRequest negativeQuantity = new UpdatePartRequest("Filtro de óleo", new BigDecimal("25.50"), UnitMeasure.UNIT,
                -1, 2, 5, "1234567890123", "top");

        Set<ConstraintViolation<UpdatePartRequest>> nullViolations = validator.validate(nullQuantity);
        Set<ConstraintViolation<UpdatePartRequest>> negativeViolations = validator.validate(negativeQuantity);

        assertThat(nullViolations)
                .extracting(ConstraintViolation::getMessage)
                .contains("não deve ser nulo");

        assertThat(negativeViolations)
                .extracting(ConstraintViolation::getMessage)
                .contains("deve ser maior que 0");
    }

    @Test
    void should_fail_when_reserved_quantity_is_null() {
        UpdatePartRequest request = new UpdatePartRequest("Filtro de óleo", new BigDecimal("25.50"), UnitMeasure.UNIT,
                10, null, 5, "1234567890123", "top");

        Set<ConstraintViolation<UpdatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve ser nulo");
    }

    @Test
    void should_fail_when_minimum_stock_quantity_is_null() {
        UpdatePartRequest request = new UpdatePartRequest("Filtro de óleo", new BigDecimal("25.50"), UnitMeasure.UNIT, 10,
                2, null, "1234567890123", "top");

        Set<ConstraintViolation<UpdatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve ser nulo");
    };

    @Test
    void should_fail_when_ean_is_blank() {
        UpdatePartRequest request = new UpdatePartRequest("Filtro de óleo", new BigDecimal("25.50"), UnitMeasure.UNIT,
                10, 2, 5, "", "top");

        Set<ConstraintViolation<UpdatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve estar em branco");
    }

    @Test
    void should_fail_when_manufacturer_is_blank() {
        UpdatePartRequest request = new UpdatePartRequest("Filtro de óleo", new BigDecimal("25.50"),
                UnitMeasure.UNIT, 10, 2, 5, "1234567890123", "");

        Set<ConstraintViolation<UpdatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve estar em branco");
    }
}