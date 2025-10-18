package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;


import java.math.BigDecimal;

public record UpdatePartRequest(
        @NotBlank String name,
        @NotNull @Positive BigDecimal unitPrice,
        @NotNull UnitMeasure unitMeasure,
        @NotNull @Positive Integer quantity,
        @NotNull Integer reservedQuantity,
        @NotNull Integer minimumStockQuantity,
        @NotBlank String ean,
        @NotBlank String manufacturer
) {}
