package soat_fiap.siaes.application.event.ServiceOrder;

import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

public record ServiceOrderFinishedEvent(
        ServiceOrder order
) {
}
