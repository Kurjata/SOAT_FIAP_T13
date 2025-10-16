package soat_fiap.siaes.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.application.event.Part.UpdateStockEvent;
import soat_fiap.siaes.domain.inventory.service.ItemService;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;

@Component
@RequiredArgsConstructor
public class PartStockListener {
    private final ItemService service;

    @Transactional
    @EventListener
    public void handle(UpdateStockEvent event) {
        for (OrderActivity item : event.order().getOrderActivities()) {
            for(OrderItem orderItem : item.getOrderItems()){
                service.updateInStock(
                        orderItem.getPartStock().getId(),
                        event.movimentType(),
                        orderItem.getQuantity(),
                        event.isRemoveReserved()
                );
            }
        }
    }
}
