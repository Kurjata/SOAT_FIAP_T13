package soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AddOrderActivityRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void should_create_valid_request() {
        AddOrderItemRequest item = new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 3);
        AddOrderActivityRequest request = new AddOrderActivityRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(item));

        Set<ConstraintViolation<AddOrderActivityRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_service_order_id_is_null() {
        AddOrderItemRequest item = new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 3);
        AddOrderActivityRequest request = new AddOrderActivityRequest(null, UUID.randomUUID(), List.of(item));

        Set<ConstraintViolation<AddOrderActivityRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("não deve ser nulo");
    }

    @Test
    void should_fail_when_service_labor_id_is_null() {
        AddOrderItemRequest item = new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 3);
        AddOrderActivityRequest request = new AddOrderActivityRequest(UUID.randomUUID(), null, List.of(item));

        Set<ConstraintViolation<AddOrderActivityRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("O ID do serviço de mão de obra é obrigatório");
    }

    @Test
    void should_fail_when_items_is_null() {
        AddOrderActivityRequest request = new AddOrderActivityRequest(UUID.randomUUID(), UUID.randomUUID(), null);

        Set<ConstraintViolation<AddOrderActivityRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("não deve ser nulo");
    }

    @Test
    void should_fail_when_items_contains_invalid_item() {
        AddOrderItemRequest invalidItem = new AddOrderItemRequest(null, null, 0);
        AddOrderActivityRequest request = new AddOrderActivityRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(invalidItem));

        Set<ConstraintViolation<AddOrderActivityRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains(
                        "O ID do item é obrigatório",
                        "A quantidade deve ser no mínimo 1"
                );
    }

    @Test
    void should_pass_when_multiple_valid_items() {
        AddOrderItemRequest i1 = new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 2);
        AddOrderItemRequest i2 = new AddOrderItemRequest(UUID.randomUUID(), UUID.randomUUID(), 5);

        AddOrderActivityRequest request = new AddOrderActivityRequest(UUID.randomUUID(), UUID.randomUUID(), List.of(i1, i2));

        Set<ConstraintViolation<AddOrderActivityRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}