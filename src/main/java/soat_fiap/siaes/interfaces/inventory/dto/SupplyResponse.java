package soat_fiap.siaes.interfaces.inventory.dto;

import soat_fiap.siaes.domain.inventory.model.Supply;

import java.math.BigDecimal;
import java.util.UUID;

public record SupplyResponse(
         UUID id,
         String name,
         BigDecimal unitPrice,
         String supplier,
         Boolean available
) {

    public static SupplyResponse fromModelSupply(Supply supply) {
        return new SupplyResponse(
                supply.getId(),
                supply.getName(),
                supply.getUnitPrice(),
                supply.getSupplier(),
                supply.getAvailable()
        );
    }
}
