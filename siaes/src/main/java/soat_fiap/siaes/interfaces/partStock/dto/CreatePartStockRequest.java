package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.domain.partStock.model.PartStock;


public record CreatePartStockRequest(

    @NotBlank String ean,
    @NotBlank String name,
    @NotNull Integer stockQuantity,
    @NotNull Integer minimumStock,
    @NotNull Double unitPrice

)
 {
    public PartStock toPartStock() {
        return new PartStock(ean, name, stockQuantity, minimumStock,unitPrice);
    }
}
