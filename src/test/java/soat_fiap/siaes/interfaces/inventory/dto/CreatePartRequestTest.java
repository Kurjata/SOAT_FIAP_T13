package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreatePartRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        CreatePartRequest request = new CreatePartRequest(10, "1234567890123", "Top", 5,
                "Filtro de óleo", new BigDecimal("29.90"), UnitMeasure.UNIT, 2);

        Set<ConstraintViolation<CreatePartRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_quantity_is_null_or_not_positive() {
        CreatePartRequest nullQuantity = new CreatePartRequest(
                null, "1234567890123", "Bosch", 5, "Filtro de óleo",
                new BigDecimal("29.90"), UnitMeasure.UNIT, 2
        );
        CreatePartRequest zeroQuantity = new CreatePartRequest(
                0, "1234567890123", "Bosch", 5, "Filtro de óleo",
                new BigDecimal("29.90"), UnitMeasure.UNIT, 2
        );

        assertThat(validator.validate(nullQuantity))
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must not be null");

        assertThat(validator.validate(zeroQuantity))
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("must be greater than 0");
    }

    @Test
    void should_fail_when_ean_is_blank() {
        CreatePartRequest request = new CreatePartRequest(
                10, "", "Bosch", 5, "Filtro de óleo",
                new BigDecimal("29.90"), UnitMeasure.UNIT, 2
        );

        Set<ConstraintViolation<CreatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be blank");
    }

    @Test
    void should_fail_when_manufacturer_is_blank() {
        CreatePartRequest request = new CreatePartRequest(
                10, "1234567890123", "", 5, "Filtro de óleo",
                new BigDecimal("29.90"), UnitMeasure.UNIT, 2
        );

        Set<ConstraintViolation<CreatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be blank");
    }

    @Test
    void should_fail_when_minimum_stock_is_null() {
        CreatePartRequest request = new CreatePartRequest(
                10, "1234567890123", "Bosch", null, "Filtro de óleo",
                new BigDecimal("29.90"), UnitMeasure.UNIT, 2
        );

        Set<ConstraintViolation<CreatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void should_fail_when_name_is_blank() {
        CreatePartRequest request = new CreatePartRequest(
                10, "1234567890123", "Bosch", 5, "",
                new BigDecimal("29.90"), UnitMeasure.UNIT, 2
        );

        Set<ConstraintViolation<CreatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be blank");
    }

    @Test
    void should_fail_when_unit_price_is_null_or_not_positive() {
        CreatePartRequest nullPrice = new CreatePartRequest(
                10, "1234567890123", "Bosch", 5, "Filtro de óleo",
                null, UnitMeasure.UNIT, 2
        );
        CreatePartRequest zeroPrice = new CreatePartRequest(
                10, "1234567890123", "Bosch", 5, "Filtro de óleo",
                BigDecimal.ZERO, UnitMeasure.UNIT, 2
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
        CreatePartRequest request = new CreatePartRequest(
                10, "1234567890123", "Bosch", 5, "Filtro de óleo",
                new BigDecimal("29.90"), null, 2
        );

        Set<ConstraintViolation<CreatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void should_fail_when_reserved_quantity_is_null() {
        CreatePartRequest request = new CreatePartRequest(
                10, "1234567890123", "Bosch", 5, "Filtro de óleo",
                new BigDecimal("29.90"), UnitMeasure.UNIT, null
        );

        Set<ConstraintViolation<CreatePartRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("must not be null");
    }

    @Test
    void should_convert_to_model() {
        CreatePartRequest request = new CreatePartRequest(10, "1234567890123", "Top", 5,
                "Filtro de óleo", new BigDecimal("29.90"), UnitMeasure.UNIT, 2);

        Part part = request.toModel();

        assertEquals("Filtro de óleo", part.getName());
        assertEquals(new BigDecimal("29.90"), part.getUnitPrice());
        assertEquals(UnitMeasure.UNIT, part.getUnitMeasure());
        assertEquals(10, part.getQuantity());
        assertEquals(2, part.getReservedQuantity());
        assertEquals("1234567890123", part.getEan());
        assertEquals("Top", part.getManufacturer());
        assertEquals(5, part.getMinimumStockQuantity());
    }
}