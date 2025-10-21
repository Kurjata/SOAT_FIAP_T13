package soat_fiap.siaes.domain.inventory.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StockMovementTest {

    private Part part;

    @BeforeEach
    void setUp() {
        part = mock(Part.class);
        when(part.getUnitPrice()).thenReturn(BigDecimal.valueOf(10));
    }

    @Test
    void calculateTotalPrice__should_return_quantity_multiplied_by_unit_price() {
        StockMovement movement = new StockMovement(part, MovementType.SAIDA_OS, 8, 20, 12);
        BigDecimal result = movement.calculateTotalPrice();
        assertEquals(new BigDecimal(80), result);
    }
}