package soat_fiap.siaes.application.event.part;

import soat_fiap.siaes.domain.inventory.model.MovementType;
import soat_fiap.siaes.domain.inventory.model.Part;

import java.util.UUID;

public record StockMovementEvent(UUID id, MovementType movementType, int quantity, int balanceBefore, int balanceAfter) {
    public StockMovementEvent(Part part, MovementType movementType, int quantity){
        this(part.getId(), movementType, quantity, part.getQuantity(), calculateAfter(part.getQuantity(), movementType, quantity));
    }

    private static int calculateAfter(int before, MovementType type, int quantity) {
        return type == MovementType.SAIDA_OS
                ? before - quantity
                : before + quantity;
    }
}