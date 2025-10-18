package soat_fiap.siaes.interfaces.inventory.dto;

import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;

import java.math.BigDecimal;
import java.util.UUID;

public record SupplyResponse(
        String id,
        String name,
        BigDecimal unitPrice,
        UnitMeasure unitMeasure,
        String supplier,
        Boolean available
) {
    public SupplyResponse(Supply supply) {
        this(supply.getIdAsString(), supply.getName(), supply.getUnitPrice(),
                supply.getUnitMeasure(), supply.getSupplier(), supply.getAvailable());
    }
}
