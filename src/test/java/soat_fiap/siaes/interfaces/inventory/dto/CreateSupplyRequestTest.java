package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateSupplyRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        CreateSupplyRequest request = new CreateSupplyRequest(
                "Óleo Lubrificante", "Petrobras",
                new BigDecimal("59.90"), UnitMeasure.LT, true
        );

        Set<ConstraintViolation<CreateSupplyRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_name_is_blank() {
        CreateSupplyRequest request = new CreateSupplyRequest(
                "", "Petrobras",
                new BigDecimal("59.90"), UnitMeasure.LT, true
        );

        Set<ConstraintViolation<CreateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve estar em branco");
    }

    @Test
    void should_fail_when_supplier_is_blank() {
        CreateSupplyRequest request = new CreateSupplyRequest(
                "Óleo Lubrificante", "",
                new BigDecimal("59.90"), UnitMeasure.LT, true
        );

        Set<ConstraintViolation<CreateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve estar em branco");
    }

    @Test
    void should_fail_when_unit_price_is_null_or_not_positive() {
        CreateSupplyRequest nullPrice = new CreateSupplyRequest(
                "Óleo Lubrificante", "Petrobras",
                null, UnitMeasure.LT, true
        );
        CreateSupplyRequest zeroPrice = new CreateSupplyRequest(
                "Óleo Lubrificante", "Petrobras",
                BigDecimal.ZERO, UnitMeasure.LT, true
        );

        assertThat(validator.validate(nullPrice))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve ser nulo");

        assertThat(validator.validate(zeroPrice))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("deve ser maior que 0");
    }

    @Test
    void should_fail_when_unit_measure_is_null() {
        CreateSupplyRequest request = new CreateSupplyRequest(
                "Óleo Lubrificante", "Petrobras",
                new BigDecimal("59.90"), null, true
        );

        Set<ConstraintViolation<CreateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve ser nulo");
    }

    @Test
    void should_fail_when_available_is_null() {
        CreateSupplyRequest request = new CreateSupplyRequest(
                "Óleo Lubrificante", "Petrobras",
                new BigDecimal("59.90"), UnitMeasure.LT, null
        );

        Set<ConstraintViolation<CreateSupplyRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("não deve ser nulo");
    }

    @Test
    void should_convert_to_model() {
        CreateSupplyRequest request = new CreateSupplyRequest(
                "Óleo Lubrificante", "Petrobras",
                new BigDecimal("59.90"), UnitMeasure.LT, true
        );

        Supply supply = request.toModel();

        assertEquals("Óleo Lubrificante", supply.getName());
        assertEquals("Petrobras", supply.getSupplier());
        assertEquals(new BigDecimal("59.90"), supply.getUnitPrice());
        assertEquals(UnitMeasure.LT, supply.getUnitMeasure());
        assertTrue(supply.getAvailable());
    }
}