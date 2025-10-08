package soat_fiap.siaes.interfaces.partStock.dto;

import soat_fiap.siaes.domain.partStock.model.Part;

import java.math.BigDecimal;
import java.util.UUID;

public record PartResponse(
        UUID id,
        String name,
        BigDecimal unitPrice,
        Integer quantity,
        String ean,
        String manufacturer,
        Integer minimumStockQuantity,
        Integer reservedQuantity
) {
    public static PartResponse fromModel(Part part) {
        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getUnitPrice(),
                part.getQuantity(),
                part.getEan(),
                part.getManufacturer(),
                part.getMinimumStockQuantity(),
                part.getReservedQuantity()
        );
    }
}
