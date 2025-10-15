package soat_fiap.siaes.application.event.Part;

import soat_fiap.siaes.domain.inventory.enums.MovimentType;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

public record UpdateStockEvent(
        ServiceOrder order,
        MovimentType movimentType,
        Boolean isRemoveReserved
) {
}
