package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;

import java.math.BigDecimal;

public record CreateSupplyRequest(
        @NotBlank String name,
        @NotBlank String supplier,
        @NotNull @Positive BigDecimal unitPrice,
        @NotNull UnitMeasure unitMeasure,
        @NotNull Boolean available
) {
    public Supply toModel() {
        return new Supply(name, unitPrice, unitMeasure, supplier, available);
    }
}
