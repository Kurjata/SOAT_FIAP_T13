package soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ServiceOrderItemSupplyRequest(
        UUID serviceOrderItemId,

        @NotNull(message = "O ID do insumo é obrigatório")
        UUID partStockId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        Integer quantity
) {
}
