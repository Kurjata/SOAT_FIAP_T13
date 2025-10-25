package soat_fiap.siaes.application.listener.part;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.application.event.part.StockMovementEvent;
import soat_fiap.siaes.domain.inventory.service.StockMovementService;

@Component
public class StockMovementListener {
    private final StockMovementService stockMovementService;

    public StockMovementListener(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @Transactional
    @EventListener
    public void handle(StockMovementEvent event) {
        stockMovementService.registerMovement(event.id(), event.movementType(), event.quantity(), event.balanceBefore(), event.balanceAfter());
    }
}