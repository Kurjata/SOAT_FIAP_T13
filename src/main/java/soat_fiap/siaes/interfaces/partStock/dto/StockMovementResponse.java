package soat_fiap.siaes.interfaces.partStock.dto;

import soat_fiap.siaes.domain.partStock.model.MovementType;
import soat_fiap.siaes.domain.partStock.model.StockMovement;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementResponse(UUID id,
                                    UUID partId,
                                    String partName,
                                    MovementType type,
                                    Integer quantity,
                                    Integer balanceAfter,
                                    BigDecimal unitPrice,
                                    BigDecimal totalValue,
                                    UUID orderId,
                                    LocalDateTime createdAt
) {
    public StockMovementResponse(StockMovement movement) {
        this(
                movement.getId(),
                movement.getPart().getId(),
                movement.getPart().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getBalanceAfter(),
                movement.getUnitPrice(),
                movement.getTotalValue(),
                movement.getOrderId(),
                movement.getCreatedAt()
        );
    }
}
