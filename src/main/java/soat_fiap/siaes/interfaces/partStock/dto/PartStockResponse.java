package soat_fiap.siaes.interfaces.partStock.dto;

import soat_fiap.siaes.domain.partStock.model.PartStock;

public record PartStockResponse(
        String id,
        String ean,
        String name,
        Integer stockQuantity,
        Integer minimumStock,
        Double unitPrice,
        boolean supply,
        boolean stockControl) {
    public PartStockResponse(PartStock partStock) {
        this(
                partStock.getIdAsString(),
                partStock.getEan(),
                partStock.getName(),
                partStock.getStockQuantity(),
                partStock.getMinimumStock(),
                partStock.getUnitPrice(),
                partStock.isSupply(),
                partStock.isStockControl()
        );
    }
}
