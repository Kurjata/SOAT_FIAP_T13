package soat_fiap.siaes.application.event.part;

import java.util.UUID;

public record StockBelowMinimumEvent(UUID id, String name, Integer quantity, Integer minimumStockQuantity) {
}