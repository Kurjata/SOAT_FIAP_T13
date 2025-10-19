package soat_fiap.siaes.domain.inventory.model;

import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.inventory.enums.ItemType;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartTest {

    private Part createPart(Integer quantity, Integer reserved, Integer minStock) {
        return new Part("Parafuso", BigDecimal.valueOf(0.50), UnitMeasure.UNIT, quantity, reserved, "1234567890123", "ABC Indústria", minStock);
    }

    @Test
    void add__Stock__should_increase_quantity() {
        Part part = createPart(10, 2, 5);

        part.addStock(5);

        assertThat(part.getQuantity()).isEqualTo(15);
    }

    @Test
    void add__Stock__should_throw_exception_when_amount_is_zero_or_negative() {
        Part part = createPart(10, 2, 5);

        assertThatThrownBy(() -> part.addStock(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");

        assertThatThrownBy(() -> part.addStock(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantidade deve ser maior que zero");
    }

    @Test
    void adjustStock__should_increase_or_decrease_quantity() {
        Part part = createPart(10, 0, 5);

        part.adjustStock(5);
        assertThat(part.getQuantity()).isEqualTo(15);

        part.adjustStock(-3);
        assertThat(part.getQuantity()).isEqualTo(12);
    }

    @Test
    void adjustStock__should_throw_exception_when_quantity_is_negative_result() {
        Part part = createPart(5, 0, 5);

        assertThatThrownBy(() -> part.adjustStock(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O estoque não pode ficar negativo.");
    }

    @Test
    void remove__Stock__should_decrease_quantity_safely() {
        Part part = createPart(10, 0, 5);

        part.removeStock(3);
        assertThat(part.getQuantity()).isEqualTo(7);

        part.removeStock(10);
        assertThat(part.getQuantity()).isEqualTo(0);
    }

    @Test
    void getType__should_return_PART() {
        Part part = createPart(10, 0, 5);

        assertThat(part.getType()).isEqualTo(ItemType.PART);
    }
}