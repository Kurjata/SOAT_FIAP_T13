package soat_fiap.siaes.application.event.Part;

import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

public record UpdateStockReservEvent(
        ServiceOrder order,
        StockOperation stockOperation
) {
}
