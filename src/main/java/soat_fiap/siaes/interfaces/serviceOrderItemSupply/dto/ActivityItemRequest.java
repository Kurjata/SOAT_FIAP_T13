package soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActivityItemRequest(
        UUID serviceOrderItemId,
        @NotNull(message = "O ID do item é obrigatório")
        UUID itemId,
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        Integer quantity
) {
}
