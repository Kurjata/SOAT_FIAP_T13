package soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;

import java.util.List;
import java.util.UUID;

public record AddOrderActivityRequest(
        @NotNull
        UUID serviceOrderId,
        @NotNull(message = "O ID do serviço de mão de obra é obrigatório")
        UUID serviceLaborId,
        @Valid
        @NotNull
        List<AddOrderItemRequest> items
) {
}