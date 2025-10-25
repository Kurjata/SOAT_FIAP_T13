package soat_fiap.siaes.interfaces.serviceOrder.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderActivityRequest(
        UUID serviceOrderId,
        @NotNull(message = "O ID do serviço de mão de obra é obrigatório")
        UUID serviceLaborId,
        @Valid
        List<OrdemItemRequest> items
) {
}
