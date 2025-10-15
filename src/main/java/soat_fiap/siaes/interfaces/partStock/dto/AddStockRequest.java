package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddStockRequest(
        @NotNull @Positive Integer quantity
) {
}
