package soat_fiap.siaes.interfaces.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.NotBlank;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;

import java.math.BigDecimal;

public record CreatePartRequest(

        @NotNull @Positive Integer quantity,
        @NotBlank String ean,
        @NotBlank String manufacturer,
        @NotNull Integer minimumStockQuantity,
        @NotBlank String name,
        @NotNull @Positive BigDecimal unitPrice,
        @NotNull UnitMeasure unitMeasure,
        @NotNull Integer reservedQuantity
) {

    public Part toModel() {

        return new Part(name, unitPrice, unitMeasure, quantity, reservedQuantity, ean, manufacturer, minimumStockQuantity);
    }
}
