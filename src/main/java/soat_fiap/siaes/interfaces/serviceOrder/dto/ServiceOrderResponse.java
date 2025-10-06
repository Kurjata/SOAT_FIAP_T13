package soat_fiap.siaes.interfaces.serviceOrder.dto;

import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.ServiceOrderItemResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ServiceOrderResponse(
        String id,
        String vehiclePlate,
        String userName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long durationMinutes,
        String orderStatus,
        List<ServiceOrderItemResponse> items
) {
    public ServiceOrderResponse(ServiceOrder order) {
        this(
                order.getId().toString(),
                order.getVehicle() != null ? order.getVehicle().getPlate() : null,
                order.getUser() != null ? order.getUser().getName() : null,
                order.getStartTime(),
                order.getEndTime(),
                order.getDurationMinutes(),
                order.getOrderStatusEnum() != null ? order.getOrderStatusEnum().getDescricao() : null,
                order.getItems() != null ? order.getItems().stream()
                        .map(ServiceOrderItemResponse::new)
                        .collect(Collectors.toList())
                        : List.of()
        );
    }
}
