package soat_fiap.siaes.interfaces.vehicle.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateVehicleRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        CreateVehicleRequest request = new CreateVehicleRequest("ABC1234", "Toyota", "Corolla", 2023);
        Set<ConstraintViolation<CreateVehicleRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_plate_is_empty() {
        CreateVehicleRequest request = new CreateVehicleRequest("", "Toyota", "Corolla", 2023);
        Set<ConstraintViolation<CreateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("A placa é obrigatória", "Placa inválida. Use o formato AAA-1234 ou AAA1A23.");
    }

    @Test
    void should_fail_when_plate_is_invalid() {
        CreateVehicleRequest request = new CreateVehicleRequest("INVALID", "Toyota", "Corolla", 2023);
        Set<ConstraintViolation<CreateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void should_fail_when_brand_is_empty() {
        CreateVehicleRequest request = new CreateVehicleRequest("ABC1234", "", "Corolla", 2023);
        Set<ConstraintViolation<CreateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("A marca é obrigatória", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_model_is_empty() {
        CreateVehicleRequest request = new CreateVehicleRequest("ABC1234", "Toyota", "", 2023);
        Set<ConstraintViolation<CreateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("O modelo é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_year_is_null() {
        CreateVehicleRequest request = new CreateVehicleRequest("ABC1234", "Toyota", "Corolla", null);
        Set<ConstraintViolation<CreateVehicleRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("O ano é obrigatório", violations.iterator().next().getMessage());
    }

    @Test
    void should_convert_to_model() {
        CreateVehicleRequest request = new CreateVehicleRequest("ABC1234", "Toyota", "Corolla", 2023);
        Vehicle vehicle = request.toModel();

        assertEquals("ABC1234", vehicle.getPlate());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2023, vehicle.getYear());
    }
}