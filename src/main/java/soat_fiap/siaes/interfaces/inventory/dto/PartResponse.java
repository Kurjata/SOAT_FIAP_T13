package soat_fiap.siaes.interfaces.inventory.dto;

import soat_fiap.siaes.domain.inventory.model.Part;

import java.math.BigDecimal;

public record PartResponse(
        String id,
        String name,
        BigDecimal unitPrice,
        Integer quantity,
        String ean,
        String manufacturer,
        Integer minimumStockQuantity,
        Integer reservedQuantity
) {
    public PartResponse(Part part) {
        this(part.getIdAsString(), part.getName(), part.getUnitPrice(), part.getQuantity(), part.getEan(),
                part.getManufacturer(), part.getMinimumStockQuantity(), part.getReservedQuantity());
    }
}
