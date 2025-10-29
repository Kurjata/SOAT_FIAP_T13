package soat_fiap.siaes.interfaces.serviceOrder.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.CreateOrderActivityRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ServiceOrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        CreateOrderActivityRequest activityRequest = new CreateOrderActivityRequest(UUID.randomUUID(), List.of());
        ServiceOrderRequest request = new ServiceOrderRequest("ABC1D23", "384.088.920-02", List.of(activityRequest));

        Set<ConstraintViolation<ServiceOrderRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "A123BCD", "1234567", "ZZZZZZZ", "AAA0000"})
    void should_fail_when_vehicle_plate_is_invalid(String invalidPlate) {
        CreateOrderActivityRequest activityRequest = new CreateOrderActivityRequest(UUID.randomUUID(), List.of());
        ServiceOrderRequest request = new ServiceOrderRequest(invalidPlate, "384.088.920-02", List.of(activityRequest));

        Set<ConstraintViolation<ServiceOrderRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Placa inválida. Use o formato AAA-1234 ou AAA1A23.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "111.111.111-11", "12.345.678/0001-9", "abcdefghijk"})
    void should_fail_when_user_document_is_invalid(String invalidDocument) {
        CreateOrderActivityRequest activityRequest = new CreateOrderActivityRequest(UUID.randomUUID(), List.of());
        ServiceOrderRequest request = new ServiceOrderRequest("ABC1D23", invalidDocument, List.of(activityRequest));

        Set<ConstraintViolation<ServiceOrderRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Documento deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos) válido");
    }

    @Test
    void should_fail_when_order_activities_is_null() {
        ServiceOrderRequest request = new ServiceOrderRequest("ABC1D23", "384.088.920-02", null);

        Set<ConstraintViolation<ServiceOrderRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("não deve ser nulo");
    }

    @Test
    void should_fail_when_order_activities_contains_invalid_activity() {
        CreateOrderActivityRequest invalidActivity = new CreateOrderActivityRequest(null, List.of());
        ServiceOrderRequest request = new ServiceOrderRequest("ABC1D23", "384.088.920-02", List.of(invalidActivity));

        Set<ConstraintViolation<ServiceOrderRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void should_pass_when_multiple_valid_activities() {
        CreateOrderActivityRequest a1 = new CreateOrderActivityRequest(UUID.randomUUID(), List.of());
        CreateOrderActivityRequest a2 = new CreateOrderActivityRequest(UUID.randomUUID(), List.of());

        ServiceOrderRequest request = new ServiceOrderRequest("XYZ2A34", "384.088.920-02", List.of(a1, a2));

        Set<ConstraintViolation<ServiceOrderRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}