package soat_fiap.siaes.interfaces.serviceOrder.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ActivityItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderActivityRequestTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_Pass_ValidationWithValidData() {
        UUID serviceOrderId = UUID.randomUUID();
        UUID serviceLaborId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        ActivityItemRequest item = new ActivityItemRequest(null, itemId, 2);
        OrderActivityRequest request = new OrderActivityRequest(serviceOrderId, serviceLaborId, List.of(item));

        Set<ConstraintViolation<OrderActivityRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    void shouldFailValidationWhenServiceLaborIdIsNull() {
        UUID itemId = UUID.randomUUID();
        ActivityItemRequest item = new ActivityItemRequest(null, itemId, 1);
        OrderActivityRequest request = new OrderActivityRequest(UUID.randomUUID(), null, List.of(item));

        Set<ConstraintViolation<OrderActivityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("serviceLaborId")));
    }

    @Test
    void shouldFailValidationWhenItemIdIsNull() {
        ActivityItemRequest item = new ActivityItemRequest(null, null, 1);
        OrderActivityRequest request = new OrderActivityRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(item));

        Set<ConstraintViolation<OrderActivityRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty()); // Espera que haja erro
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items[0].itemId")));
    }

    @Test
    void shouldFailValidationWhenQuantityIsNull() {
        ActivityItemRequest item = new ActivityItemRequest(null, UUID.randomUUID(), null);
        OrderActivityRequest request = new OrderActivityRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(item));

        Set<ConstraintViolation<OrderActivityRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items[0].quantity")));
    }

    @Test
    void shouldFailValidationWhenQuantityIsLessThanOne() {
        ActivityItemRequest item = new ActivityItemRequest(null, UUID.randomUUID(), 0);
        OrderActivityRequest request = new OrderActivityRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(item));

        Set<ConstraintViolation<OrderActivityRequest>> violations = validator.validate(request);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items[0].quantity")));
    }
}
