package soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateOrderActivityRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        CreateOrderItemRequest item = new CreateOrderItemRequest(UUID.randomUUID(), 2);
        CreateOrderActivityRequest request = new CreateOrderActivityRequest(UUID.randomUUID(), List.of(item));

        Set<ConstraintViolation<CreateOrderActivityRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_service_labor_id_is_null() {
        CreateOrderItemRequest item = new CreateOrderItemRequest(UUID.randomUUID(), 2);
        CreateOrderActivityRequest request = new CreateOrderActivityRequest(null, List.of(item));

        Set<ConstraintViolation<CreateOrderActivityRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("O ID do serviço de mão de obra é obrigatório");
    }

    @Test
    void should_fail_when_items_is_null() {
        CreateOrderActivityRequest request = new CreateOrderActivityRequest(UUID.randomUUID(), null);

        Set<ConstraintViolation<CreateOrderActivityRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("não deve ser nulo");
    }

    @Test
    void should_fail_when_items_contains_invalid_item() {
        CreateOrderItemRequest invalidItem = new CreateOrderItemRequest(null, 0);
        CreateOrderActivityRequest request = new CreateOrderActivityRequest(UUID.randomUUID(), List.of(invalidItem));

        Set<ConstraintViolation<CreateOrderActivityRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void should_pass_when_multiple_valid_items() {
        CreateOrderItemRequest i1 = new CreateOrderItemRequest(UUID.randomUUID(), 2);
        CreateOrderItemRequest i2 = new CreateOrderItemRequest(UUID.randomUUID(), 5);

        CreateOrderActivityRequest request = new CreateOrderActivityRequest(UUID.randomUUID(), List.of(i1, i2));

        Set<ConstraintViolation<CreateOrderActivityRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}