package soat_fiap.siaes.interfaces.serviceOrderItem.dto;

import soat_fiap.siaes.domain.serviceOrderItem.model.ServiceOrderItem;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyResponse;

import java.util.List;
import java.util.stream.Collectors;

public record ServiceOrderItemResponse(
        ServiceLaborResponse serviceLabor,
        List<ServiceOrderItemSupplyResponse> supplies
) {
    public ServiceOrderItemResponse(ServiceOrderItem item) {
        this(
                new ServiceLaborResponse(item.getServiceLabor()),
                item.getSupplies() != null ? item.getSupplies().stream()
                        .map(ServiceOrderItemSupplyResponse::new)
                        .collect(Collectors.toList())
                        : List.of()
        );
    }
}