package soat_fiap.siaes.interfaces.serviceOrderItem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyRequest;

import java.util.List;
import java.util.UUID;

public record ServiceOrderItemRequest(
        UUID serviceOrderId, // Id da ordem de serviço, opcional
        @NotNull(message = "O ID do serviço de mão de obra é obrigatório")
        UUID serviceLaborId, // id do serviço
        @Valid
        List<ServiceOrderItemSupplyRequest> supplies // Lista dos insumos
) {
}
