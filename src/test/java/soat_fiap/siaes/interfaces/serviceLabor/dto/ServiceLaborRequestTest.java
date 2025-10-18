package soat_fiap.siaes.interfaces.serviceLabor.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ServiceLaborRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", new BigDecimal("150.00"));
        Set<ConstraintViolation<ServiceLaborRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_description_is_blank() {
        ServiceLaborRequest request = new ServiceLaborRequest("", new BigDecimal("150.00"));
        Set<ConstraintViolation<ServiceLaborRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("A descrição da mão de obra é obrigatória.",
                        "A descrição deve ter entre 3 e 100 caracteres.");
    }

    @Test
    void should_fail_when_description_is_too_short() {
        ServiceLaborRequest request = new ServiceLaborRequest("OK", new BigDecimal("150.00"));
        Set<ConstraintViolation<ServiceLaborRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("A descrição deve ter entre 3 e 100 caracteres.",
                violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_description_is_too_long() {
        String longDescription = "A".repeat(101);
        ServiceLaborRequest request = new ServiceLaborRequest(longDescription, new BigDecimal("150.00"));
        Set<ConstraintViolation<ServiceLaborRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("A descrição deve ter entre 3 e 100 caracteres.",
                violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_laborCost_is_null() {
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", null);
        Set<ConstraintViolation<ServiceLaborRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("O custo da mão de obra é obrigatório.", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_laborCost_is_zero_or_negative() {
        ServiceLaborRequest requestZero = new ServiceLaborRequest("Troca de óleo", BigDecimal.ZERO);
        ServiceLaborRequest requestNegative = new ServiceLaborRequest("Troca de óleo", new BigDecimal("-10.00"));

        Set<ConstraintViolation<ServiceLaborRequest>> violationsZero = validator.validate(requestZero);
        Set<ConstraintViolation<ServiceLaborRequest>> violationsNegative = validator.validate(requestNegative);

        assertFalse(violationsZero.isEmpty());
        assertFalse(violationsNegative.isEmpty());

        assertThat(violationsZero)
                .extracting(ConstraintViolation::getMessage)
                .contains("O custo da mão de obra deve ser maior que zero.");
        assertThat(violationsNegative)
                .extracting(ConstraintViolation::getMessage)
                .contains("O custo da mão de obra deve ser maior que zero.");
    }

    @Test
    void should_fail_when_laborCost_has_too_many_digits() {
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", new BigDecimal("12345678901.123"));
        Set<ConstraintViolation<ServiceLaborRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("O custo da mão de obra deve ter no máximo 10 dígitos inteiros e 2 decimais.",
                violations.iterator().next().getMessage());
    }

}