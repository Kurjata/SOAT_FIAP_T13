package soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto;

import soat_fiap.siaes.domain.partStock.enums.ItemType;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ActivityItem;

import java.math.BigDecimal;

public record ServiceOrderItemSupplyResponse(
        String id,
        String ean,
        String name,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
    public ServiceOrderItemSupplyResponse(ActivityItem supply) {
        this(
                supply.getPartStock().getIdAsString(),
                ItemType.PART.equals(supply.getPartStock().getType())
                        ? ((Part) supply.getPartStock()).getEan()
                        : "NaN",
                supply.getPartStock().getName(),
                supply.getQuantity(),
                supply.getUnitPrice(),
                supply.getUnitPrice().multiply(BigDecimal.valueOf(supply.getQuantity()))
        );
    }
}
