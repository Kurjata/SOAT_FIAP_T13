package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import soat_fiap.siaes.domain.partStock.model.Part;


import java.math.BigDecimal;

public record UpdatePartRequest(

        @NotNull @Positive Integer quantity,
        @NotBlank String ean,
        @NotBlank String manufacturer,
        @NotNull Integer minimumStockQuantity,
        @NotBlank String name,
        @NotNull @Positive BigDecimal unitPrice) {

    public void applyTo(Part part) {
        part.update(name, unitPrice, quantity, ean, manufacturer, minimumStockQuantity);
    }
}
