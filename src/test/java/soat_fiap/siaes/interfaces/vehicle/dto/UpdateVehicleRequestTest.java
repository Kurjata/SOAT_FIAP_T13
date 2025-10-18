package soat_fiap.siaes.interfaces.vehicle.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UpdateVehicleRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("ABC1234", "Toyota", "Corolla", 2023);
        Set<ConstraintViolation<UpdateVehicleRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_plate_is_empty() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("", "Toyota", "Corolla", 2023);
        Set<ConstraintViolation<UpdateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("A placa é obrigatória", "Placa inválida. Use o formato AAA-1234 ou AAA1A23.");
    }

    @Test
    void should_fail_when_plate_is_invalid() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("INVALID", "Toyota", "Corolla", 2023);
        Set<ConstraintViolation<UpdateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void should_fail_when_brand_is_empty() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("ABC1234", "", "Corolla", 2023);
        Set<ConstraintViolation<UpdateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("A marca é obrigatória", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_model_is_empty() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("ABC1234", "Toyota", "", 2023);
        Set<ConstraintViolation<UpdateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("O modelo é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_year_is_null() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("ABC1234", "Toyota", "Corolla", null);
        Set<ConstraintViolation<UpdateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("O ano é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void should_remove_hyphen_from_plate() {
        UpdateVehicleRequest request = new UpdateVehicleRequest("ABC-1234", "Toyota", "Corolla", 2023);
        assertEquals("ABC1234", request.plate());
    }
}