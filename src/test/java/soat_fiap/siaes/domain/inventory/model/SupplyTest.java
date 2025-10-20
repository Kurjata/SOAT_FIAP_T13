package soat_fiap.siaes.domain.inventory.model;

import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.shared.BusinessException;

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

    @Test
    void handleStockOperation_should_throw_exception_when_not_available() {
        Supply supply = new Supply("Teste", BigDecimal.valueOf(10), UnitMeasure.UNIT, "Fornecedor", false);

        assertThatThrownBy(() -> supply.handleStockOperation(StockOperation.RESERVE_STOCK, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não há insumo disponível");
    }

    @Test
    void handleStockOperation_should_do_nothing_when_available() {
        Supply supply = new Supply("Teste", BigDecimal.valueOf(10), UnitMeasure.UNIT, "Fornecedor", true);

        supply.handleStockOperation(StockOperation.RESERVE_STOCK, 5);
    }
}