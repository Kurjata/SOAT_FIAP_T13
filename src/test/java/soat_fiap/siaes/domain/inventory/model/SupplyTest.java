package soat_fiap.siaes.domain.inventory.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SupplyTest {

    @Test
    void setAvailability_should_set_value_when_not_null() {
        Supply supply = new Supply("Teste", BigDecimal.valueOf(10), UnitMeasure.UNIT, "Fornecedor", true);

        supply.setAvailability(false);
        assertThat(supply.getAvailable()).isFalse();

        supply.setAvailability(true);
        assertThat(supply.getAvailable()).isTrue();
    }

    @Test
    void setAvailability_should_throw_exception_when_null() {
        Supply supply = new Supply("Teste", BigDecimal.valueOf(10), UnitMeasure.UNIT, "Fornecedor", true);

        assertThatThrownBy(() -> supply.setAvailability(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Disponibilidade deve ser informada");
    }
}