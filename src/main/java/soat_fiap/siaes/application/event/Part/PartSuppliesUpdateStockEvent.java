package soat_fiap.siaes.application.event.Part;

import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

public record PartSuppliesUpdateStockEvent(
        ServiceOrder order
) {
}
