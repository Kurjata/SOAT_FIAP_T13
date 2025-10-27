package soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;

import java.util.List;
import java.util.UUID;

public record CreateOrderActivityRequest(
        @NotNull(message = "O ID do serviço de mão de obra é obrigatório")
        UUID serviceLaborId,
        @Valid
        @NotNull
        List<CreateOrderItemRequest> items
) {
}
