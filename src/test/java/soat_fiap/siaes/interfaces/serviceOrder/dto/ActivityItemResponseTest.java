package soat_fiap.siaes.interfaces.serviceOrder.dto;

import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.inventory.enums.ItemType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActivityItemResponseTest {

    private OrderItem createMockOrderItem(ItemType type, int quantity, BigDecimal unitPrice, String ean) {

        Part mockPart = mock(Part.class);
        when(mockPart.getIdAsString()).thenReturn(UUID.randomUUID().toString());
        when(mockPart.getName()).thenReturn("Óleo Sintético 5W30");
        when(mockPart.getType()).thenReturn(type);
        when(mockPart.getEan()).thenReturn(ean);

        OrderItem mockOrderItem = mock(OrderItem.class);
        when(mockOrderItem.getPartStock()).thenReturn(mockPart);
        when(mockOrderItem.getQuantity()).thenReturn(quantity);
        when(mockOrderItem.getUnitPrice()).thenReturn(unitPrice);

        return mockOrderItem;
    }

    private OrderItem createMockOrderSupply(ItemType type, int quantity, BigDecimal unitPrice) {

        Supply mockSupply = mock(Supply.class);
        when(mockSupply.getIdAsString()).thenReturn(UUID.randomUUID().toString());
        when(mockSupply.getName()).thenReturn("Líquido de Freio DOT 4");
        when(mockSupply.getType()).thenReturn(type);

        OrderItem mockOrderItem = mock(OrderItem.class);
        when(mockOrderItem.getPartStock()).thenReturn(mockSupply);
        when(mockOrderItem.getQuantity()).thenReturn(quantity);
        when(mockOrderItem.getUnitPrice()).thenReturn(unitPrice);

        return mockOrderItem;
    }

    @Test
    void should_map_correctly_when_itemType_is_PART() {
        int quantity = 3;
        BigDecimal unitPrice = new BigDecimal("150.00");
        String expectedEan = "9876543210123";

        OrderItem orderItem = createMockOrderItem(ItemType.PART, quantity, unitPrice, expectedEan);

        ActivityItemResponse response = new ActivityItemResponse(orderItem);

        BigDecimal expectedTotalPrice = new BigDecimal("450.00"); // 3 * 150.00

        assertEquals(orderItem.getPartStock().getIdAsString(), response.id(), "O ID deve ser mapeado.");
        assertEquals(expectedEan, response.ean(), "O EAN deve ser mapeado corretamente quando o tipo é PART.");
        assertEquals(orderItem.getPartStock().getName(), response.name(), "O Nome deve ser mapeado.");
        assertEquals(quantity, response.quantity(), "A Quantidade deve ser mapeada.");
        assertEquals(unitPrice, response.unitPrice(), "O Preço Unitário deve ser mapeado.");
        assertEquals(expectedTotalPrice, response.totalPrice(), "O Preço Total deve ser calculado (Quantidade * Preço Unitário).");
    }

    @Test
    void should_map_ean_to_NaN_when_itemType_is_not_PART() {

        int quantity = 1;
        BigDecimal unitPrice = new BigDecimal("80.50");

        OrderItem orderItem = createMockOrderSupply(ItemType.SUPPLY, quantity, unitPrice);

        ActivityItemResponse response = new ActivityItemResponse(orderItem);

        BigDecimal expectedTotalPrice = new BigDecimal("80.50");

        assertEquals(orderItem.getPartStock().getIdAsString(), response.id(), "O ID deve ser mapeado.");
        assertEquals("NaN", response.ean(), "O EAN deve ser 'NaN' quando o tipo NÃO é PART.");
        assertEquals(orderItem.getPartStock().getName(), response.name(), "O Nome deve ser mapeado.");
        assertEquals(quantity, response.quantity(), "A Quantidade deve ser mapeada.");
        assertEquals(unitPrice, response.unitPrice(), "O Preço Unitário deve ser mapeado.");
        assertEquals(expectedTotalPrice, response.totalPrice(), "O Preço Total deve ser calculado.");
    }

}
