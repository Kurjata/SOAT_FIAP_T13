package soat_fiap.siaes.interfaces.partStock.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import soat_fiap.siaes.domain.partStock.model.Supply;

import java.math.BigDecimal;

public record UpdateSupplyRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal unitPrice,
        @NotBlank String supplier,
        @NotNull Boolean available) {

    public void applyToSupply(Supply supply) {
        supply.update(name, unitPrice,supplier,available);
    }

}
