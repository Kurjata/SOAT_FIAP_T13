package soat_fiap.siaes.application.event.part;

import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

public record UpdateStockEvent(
        ServiceOrder order,
        StockOperation stockOperation
) {
}
