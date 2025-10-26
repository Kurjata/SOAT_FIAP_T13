package soat_fiap.siaes.interfaces.serviceOrder.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityItemRequestTest {

    private final Validator validator;

    public ActivityItemRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve criar ActivityItemRequest com todos os campos válidos")
    void should_create_activity_item_request_with_valid_fields() {

        UUID serviceOrderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Integer quantity = 5;

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, itemId, quantity);

        assertNotNull(request);
        assertEquals(serviceOrderItemId, request.serviceOrderItemId());
        assertEquals(itemId, request.itemId());
        assertEquals(quantity, request.quantity());

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    @DisplayName("Deve criar ActivityItemRequest com serviceOrderItemId nulo")
    void should_create_activity_item_request_with_service_order_item_id_null() {

        UUID itemId = UUID.randomUUID();
        Integer quantity = 1;

        ActivityItemRequest request = new ActivityItemRequest(null, itemId, quantity);

        assertNotNull(request);
        assertNull(request.serviceOrderItemId());
        assertEquals(itemId, request.itemId());
        assertEquals(quantity, request.quantity());

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "serviceOrderItemId nulo não deve gerar violações");
    }

    @Test
    @DisplayName("Deve falhar quando itemId é nulo")
    void should_fail_when_item_id_null() {

        UUID serviceOrderItemId = UUID.randomUUID();
        Integer quantity = 1;

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, null, quantity);

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Deve haver violações quando itemId é nulo");

        assertEquals(1, violations.size());
        ConstraintViolation<ActivityItemRequest> violation = violations.iterator().next();
        assertEquals("O ID do item é obrigatório", violation.getMessage());
        assertEquals("itemId", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve falhar quando quantity é nulo")
    void should_fail_when_null_quantity() {

        UUID serviceOrderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, itemId, null);

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Deve haver violações quando quantity é nulo");

        assertEquals(1, violations.size());
        ConstraintViolation<ActivityItemRequest> violation = violations.iterator().next();
        assertEquals("A quantidade é obrigatória", violation.getMessage());
        assertEquals("quantity", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve falhar quando quantity é menor que 1")
    void should_speak_when_quantity_less_than_one() {

        UUID serviceOrderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Integer invalidQuantity = 0;

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, itemId, invalidQuantity);

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Deve haver violações quando quantity é menor que 1");

        assertEquals(1, violations.size());
        ConstraintViolation<ActivityItemRequest> violation = violations.iterator().next();
        assertEquals("A quantidade deve ser no mínimo 1", violation.getMessage());
        assertEquals("quantity", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve falhar quando quantity é negativo")
    void should_fail_when_quantity_negative() {

        UUID serviceOrderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Integer invalidQuantity = -5;

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, itemId, invalidQuantity);

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Deve haver violações quando quantity é negativo");

        assertEquals(1, violations.size());
        ConstraintViolation<ActivityItemRequest> violation = violations.iterator().next();
        assertEquals("A quantidade deve ser no mínimo 1", violation.getMessage());
        assertEquals("quantity", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Deve aceitar quantity igual a 1")
    void should_accept_quantity_equal_to_one() {

        UUID serviceOrderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Integer quantity = 1;

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, itemId, quantity);

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Deve aceitar quantity igual a 1");
    }

    @Test
    @DisplayName("Deve aceitar quantity maior que 1")
    void should_accept_quantity_greater_than_one() {

        UUID serviceOrderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Integer quantity = 100;

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, itemId, quantity);

        Set<ConstraintViolation<ActivityItemRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Deve aceitar quantity maior que 1");
    }

    @Test
    @DisplayName("Deve testar igualdade entre objetos ActivityItemRequest")
    void should_test_equality_between_objects() {

        UUID serviceOrderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Integer quantity = 5;

        ActivityItemRequest request1 = new ActivityItemRequest(serviceOrderItemId, itemId, quantity);
        ActivityItemRequest request2 = new ActivityItemRequest(serviceOrderItemId, itemId, quantity);

        assertEquals(request1, request2, "Objetos com mesmos valores devem ser iguais");
        assertEquals(request1.hashCode(), request2.hashCode(), "HashCodes devem ser iguais para objetos iguais");
    }

    @Test
    @DisplayName("Deve gerar toString corretamente")
    void should_generate_to_string_correctly() {

        UUID serviceOrderItemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID itemId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        Integer quantity = 10;

        ActivityItemRequest request = new ActivityItemRequest(serviceOrderItemId, itemId, quantity);
        String toStringResult = request.toString();

        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("serviceOrderItemId=123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(toStringResult.contains("itemId=123e4567-e89b-12d3-a456-426614174001"));
        assertTrue(toStringResult.contains("quantity=10"));
    }
}
