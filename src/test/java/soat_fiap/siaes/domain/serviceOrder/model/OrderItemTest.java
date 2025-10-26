package soat_fiap.siaes.domain.serviceOrder.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soat_fiap.siaes.domain.inventory.model.Item;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class OrderItemTest {

    @Mock
    private OrderActivity mockOrderActivity;

    @Mock
    private Item mockPartStock;

    private final UUID ORDER_ITEM_ID = UUID.randomUUID();
    private final Integer QUANTITY = 5;
    private final BigDecimal UNIT_PRICE = new BigDecimal("10.50");

    private final BigDecimal EXPECTED_TOTAL_PRICE = new BigDecimal("52.50");

    @Test
    void allArgsConstructor_should_create_instance_and_set_fields() {

        OrderItem orderItem = new OrderItem(
                ORDER_ITEM_ID,
                mockOrderActivity,
                mockPartStock,
                QUANTITY,
                UNIT_PRICE
        );

        assertNotNull(orderItem);
        assertEquals(ORDER_ITEM_ID, orderItem.getId());
        assertEquals(mockOrderActivity, orderItem.getOrderActivity());
        assertEquals(mockPartStock, orderItem.getPartStock());
        assertEquals(QUANTITY, orderItem.getQuantity());
        assertEquals(UNIT_PRICE, orderItem.getUnitPrice());
    }

    @Test
    void customConstructor_should_create_instance_without_id() {

        OrderItem orderItem = new OrderItem(
                mockOrderActivity,
                mockPartStock,
                QUANTITY,
                UNIT_PRICE
        );

        assertNotNull(orderItem);
        assertThat(orderItem.getId()).isNull();
        assertEquals(mockOrderActivity, orderItem.getOrderActivity());
        assertEquals(mockPartStock, orderItem.getPartStock());
        assertEquals(QUANTITY, orderItem.getQuantity());
        assertEquals(UNIT_PRICE, orderItem.getUnitPrice());
    }

    @Test
    void noArgsConstructor_should_create_empty_instance() {
        OrderItem orderItem = new OrderItem();

        assertNotNull(orderItem);
        assertThat(orderItem.getId()).isNull();
        assertThat(orderItem.getOrderActivity()).isNull();
        assertThat(orderItem.getPartStock()).isNull();
        assertThat(orderItem.getQuantity()).isNull();
        assertThat(orderItem.getUnitPrice()).isNull();
    }

    @Test
    void getTotalPrice_should_calculate_correct_total() {
        OrderItem orderItem = new OrderItem(
                mockOrderActivity,
                mockPartStock,
                QUANTITY,
                UNIT_PRICE
        );

        BigDecimal totalPrice = orderItem.getTotalPrice();

        assertEquals(0, totalPrice.compareTo(EXPECTED_TOTAL_PRICE),
                "O preço total deve ser Quantidade * Preço Unitário.");
    }

    @Test
    void dataAnnotation_should_cover_getters_setters_and_utility_methods() {
        OrderItem item1 = new OrderItem(ORDER_ITEM_ID, mockOrderActivity, mockPartStock, QUANTITY, UNIT_PRICE);
        OrderItem item2 = new OrderItem(ORDER_ITEM_ID, mockOrderActivity, mockPartStock, QUANTITY, UNIT_PRICE);

        OrderItem item3 = new OrderItem(UUID.randomUUID(), mockOrderActivity, mockPartStock, 1, new BigDecimal("1.00"));

        item3.setQuantity(QUANTITY);

        assertEquals(QUANTITY, item3.getQuantity());

        assertThat(item1).isEqualTo(item2);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
        assertThat(item1).isNotEqualTo(item3);

        assertThat(item1.toString()).contains("quantity=" + QUANTITY);
    }
}
