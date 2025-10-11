package soat_fiap.siaes.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.application.event.Part.UpdateStockEvent;
import soat_fiap.siaes.domain.partStock.service.ItemService;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ActivityItem;

@Component
@RequiredArgsConstructor
public class PartStockListener {
    private final ItemService service;

    @Transactional
    @EventListener
    public void handle(UpdateStockEvent event) {
        for (OrderActivity item : event.order().getOrderActivities()) {
            for(ActivityItem activityItem : item.getActivityItems()){
                service.updateInStock(
                        activityItem.getPartStock().getId(),
                        event.movimentTypeEnum(),
                        activityItem.getQuantity(),
                        event.isRemoveReserved()
                );
            }
        }
    }
}
