package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePartStockRequest(
        @NotBlank String ean,
        @NotBlank String name,
        @NotNull Integer stockQuantity,
        @NotNull Integer minimumStock,
        @NotNull Double unitPrice,
        @NotNull boolean supply,
        @NotNull boolean stockControl
) {
}
