package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.domain.partStock.model.MovementType;

import java.util.UUID;

public record CreateStockMovementRequest(
        @NotNull UUID partId,
        @NotNull MovementType type,
        @Min(1) Integer quantity,
        UUID orderId ) {}
