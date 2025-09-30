package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.domain.partStock.model.PartStock;


public record CreatePartStockRequest(

    @NotBlank String ean,
    @NotBlank String name,
    @NotNull Integer stockQuantity,
    @NotNull Integer minimumStock,
    @NotNull Double unitPrice,
    @NotNull Boolean supply,
    @NotNull Boolean stockControl
)
 {
     public PartStock toPartStock() {
         PartStock part = new PartStock();
         part.setEan(ean);
         part.setName(name);
         part.setStockQuantity(stockQuantity);
         part.setMinimumStock(minimumStock);
         part.setUnitPrice(unitPrice);
         part.setSupply(supply);
         part.setStockControl(stockControl);
         return part;
     }
}
