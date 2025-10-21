package soat_fiap.siaes.domain.inventory.model;

import org.junit.jupiter.api.Test;
import soat_fiap.siaes.application.event.part.StockBelowMinimumEvent;
import soat_fiap.siaes.application.event.part.StockMovementEvent;
import soat_fiap.siaes.domain.inventory.enums.ItemType;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.shared.BusinessException;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PartTest {

    private Part createPart(Integer quantity, Integer reserved, Integer minStock) {
        return new Part("Parafuso", BigDecimal.valueOf(0.50), UnitMeasure.UNIT, quantity, reserved, "1234567890123", "ABC Indústria", minStock);
    }

    @Test
    void addStock__should_increase_quantity_and_register_stock_movement_event() {
        Part part = createPart(10, 2, 5);
        part.addStock(5);

        assertThat(part.getQuantity()).isEqualTo(15);
        List<Object> events = part.getDomainEvents();
        assertThat(events).hasSize(1).first().isInstanceOf(StockMovementEvent.class);
    }

    @Test
    void addStock__should_throw_exception_when_amount_is_zero_or_negative() {
        Part part = createPart(10, 2, 5);

        assertThatThrownBy(() -> part.addStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");

        assertThatThrownBy(() -> part.addStock(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");

        assertEquals(0, part.getDomainEvents().size());
    }

    @Test
    void removeStock__should_decrease_quantity_safely() {
        Part part = createPart(10, 0, 5);

        part.removeStock(3);
        assertThat(part.getQuantity()).isEqualTo(7);

        part.removeStock(2);
        assertThat(part.getQuantity()).isEqualTo(5);
    }

    @Test
    void removeStock__should_throw_exception_when_insufficient_stock() {
        Part part = createPart(5, 0, 5);

        assertThatThrownBy(() -> part.removeStock(10))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    void adjustStock__should_increase_or_decrease_quantity_and_register_stock_movement_event() {
        Part part = createPart(10, 0, 5);

        part.adjustStock(5);
        assertThat(part.getQuantity()).isEqualTo(15);

        part.adjustStock(-3);
        assertThat(part.getQuantity()).isEqualTo(12);

        assertThat(part.getDomainEvents())
                .hasSize(2)
                .anySatisfy(event -> assertThat(event).isInstanceOf(StockMovementEvent.class));
    }

    @Test
    void adjustStock__should_throw_exception_when_quantity_is_negative_result() {
        Part part = createPart(5, 0, 5);

        assertThatThrownBy(() -> part.adjustStock(-10))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ajuste resultaria em estoque negativo");

        assertEquals(0, part.getDomainEvents().size());
    }

    @Test
    void moveToReserved__should_move_quantity_from_available_to_reserved_and_register_stock_movement_event() {
        Part part = createPart(10, 2, 5);

        part.moveToReserved(3);

        assertThat(part.getQuantity()).isEqualTo(7);
        assertThat(part.getReservedQuantity()).isEqualTo(5);
    }

    @Test
    void moveToAvailable__should_move_quantity_from_reserved_to_available_and_register_stock_movement_event() {
        Part part = createPart(10, 2, 5);

        part.moveToAvailable(2);

        assertThat(part.getQuantity()).isEqualTo(12);
        assertThat(part.getReservedQuantity()).isEqualTo(0);
        assertThat(part.getDomainEvents()).hasSize(1).first().isInstanceOf(StockMovementEvent.class);
    }

    @Test
    void reserveStock__should_move_to_reserved_and_register_stock_movement_event() {
        Part part = createPart(10, 2, 5);

        part.reserveStock(3);

        assertThat(part.getQuantity()).isEqualTo(7);
        assertThat(part.getReservedQuantity()).isEqualTo(5);
        assertThat(part.getDomainEvents()).hasSize(1).first().isInstanceOf(StockMovementEvent.class);
    }

    @Test
    void cancelReservation__should_return_reserved_to_available_and_register_stock_movement_event() {
        Part part = createPart(10, 5, 5);

        part.cancelReservation(3);

        assertThat(part.getQuantity()).isEqualTo(13);
        assertThat(part.getReservedQuantity()).isEqualTo(2);
        assertThat(part.getDomainEvents()).hasSize(2).first().isInstanceOf(StockMovementEvent.class);
    }

    @Test
    void confirmReservation__should_decrease_reserved_quantity() {
        Part part = createPart(10, 5, 5);

        part.confirmReservation(3);

        assertThat(part.getReservedQuantity()).isEqualTo(2);
    }

    @Test
    void handleStockOperation__should_delegate_to_correct_method() {
        Part part = createPart(10, 2, 5);

        part.handleStockOperation(StockOperation.RESERVE_STOCK, 3);
        assertThat(part.getQuantity()).isEqualTo(7);
        assertThat(part.getReservedQuantity()).isEqualTo(5);

        part.handleStockOperation(StockOperation.CANCEL_RESERVATION, 2);
        assertThat(part.getQuantity()).isEqualTo(9);
        assertThat(part.getReservedQuantity()).isEqualTo(3);

        part.handleStockOperation(StockOperation.CONFIRM_RESERVATION, 1);
        assertThat(part.getReservedQuantity()).isEqualTo(2);
    }

    @Test
    void hasAvailableStock__should_return_true_or_false_if_available() {
        Part part = createPart(10, 2, 5);

        assertThat(part.hasAvailableStock(5)).isTrue();
        assertThat(part.hasAvailableStock(15)).isFalse();
    }

    @Test
    void hasAvailableReservedStock__should_return_true_or_false_if_available() {
        Part part = createPart(10, 2, 5);

        assertThat(part.hasAvailableReservedStock(2)).isTrue();
        assertThat(part.hasAvailableReservedStock(5)).isFalse();
    }

    @Test
    void isBelowMinimumStock__should_trigger_stock_and_alert_events() {
        Part part = createPart(3, 2, 5);

        part.addStock(1);
        assertThat(part.isBelowMinimumStock()).isTrue();

        part.addStock(6);
        assertThat(part.isBelowMinimumStock()).isFalse();

        assertThat(part.getDomainEvents())
                .hasSize(3)
                .anySatisfy(event -> assertThat(event).isInstanceOf(StockMovementEvent.class))
                .anySatisfy(event -> assertThat(event).isInstanceOf(StockBelowMinimumEvent.class));
    }

    @Test
    void getType__should_return_PART() {
        Part part = createPart(10, 0, 5);

        assertThat(part.getType()).isEqualTo(ItemType.PART);
    }
}