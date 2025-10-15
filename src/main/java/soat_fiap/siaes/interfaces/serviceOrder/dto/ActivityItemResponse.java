package soat_fiap.siaes.interfaces.serviceOrder.dto;

import soat_fiap.siaes.domain.inventory.enums.ItemType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;

import java.math.BigDecimal;

public record ActivityItemResponse(
        String id,
        String ean,
        String name,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
    public ActivityItemResponse(OrderItem supply) {
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
