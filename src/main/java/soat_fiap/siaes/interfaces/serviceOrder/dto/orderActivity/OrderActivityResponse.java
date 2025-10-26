package soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity;

import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.OrderItemResponse;

import java.util.List;
import java.util.stream.Collectors;

public record OrderActivityResponse(
        ServiceLaborResponse serviceLabor,
        List<OrderItemResponse> supplies
) {
    public OrderActivityResponse(OrderActivity item) {
        this(
                new ServiceLaborResponse(item.getServiceLabor()),
                item.getOrderItems() != null ? item.getOrderItems().stream()
                        .map(OrderItemResponse::new)
                        .collect(Collectors.toList())
                        : List.of()
        );
    }
}