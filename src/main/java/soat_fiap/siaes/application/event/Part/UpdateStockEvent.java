package soat_fiap.siaes.application.event.Part;

import soat_fiap.siaes.domain.partStock.enums.MovimentTypeEnum;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

public record UpdateStockEvent(
        ServiceOrder order,
        MovimentTypeEnum movimentTypeEnum
) {
}
