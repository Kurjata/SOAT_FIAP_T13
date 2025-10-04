package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.NotBlank;
import soat_fiap.siaes.domain.partStock.model.Part;

import java.math.BigDecimal;

public record CreatePartRequest(

        @NotNull @Positive Integer quantity,
        @NotBlank String ean,
        @NotBlank String manufacturer,
        @NotNull Integer minimumStockQuantity,
        @NotBlank String name,
        @NotNull @Positive BigDecimal unitPrice) {

    public Part toModel() {

        return new Part(name, unitPrice, quantity, ean, manufacturer, minimumStockQuantity);
    }
}
