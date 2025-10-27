package soat_fiap.siaes.application.event.serviceOrder;

import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;

import java.util.UUID;

public record UpdateOrderStatusEvent(UUID orderId, ServiceOrderStatus status) {
}
