package soat_fiap.siaes.application.listener.part;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.application.event.part.UpdateStockEvent;
import soat_fiap.siaes.application.event.serviceOrder.UpdateOrderStatusEvent;
import soat_fiap.siaes.domain.inventory.service.ItemService;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.shared.BusinessException;

import static soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus.AGUARDANDO_ESTOQUE;

@Component
public class UpdateStockListener {
    private final ItemService service;
    private final ApplicationEventPublisher publisher;

    public UpdateStockListener(ItemService service, ApplicationEventPublisher publisher) {
        this.service = service;
        this.publisher = publisher;
    }

    @Transactional
    @EventListener
    public void handle(UpdateStockEvent event) {
        try {
            event.order().getOrderActivities().stream()
                    .flatMap(activity -> activity.getOrderItems().stream())
                    .forEach(item -> processItemStock(item, event));
        } catch (BusinessException e) {
            publisher.publishEvent(new UpdateOrderStatusEvent(event.order().getId(), AGUARDANDO_ESTOQUE));
            throw e;
        }
    }

    private void processItemStock(OrderItem item, UpdateStockEvent event) {
        service.processStockMovement(
                item.getPartStock().getId(),
                event.stockOperation(),
                item.getQuantity()
        );
    }
}

