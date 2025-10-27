package soat_fiap.siaes.application.listener.serviceOrder;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.application.event.serviceOrder.UpdateOrderStatusEvent;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.domain.serviceOrder.service.ServiceOrderService;

@Component
public class UpdateOrderStatusListener {
    private final ServiceOrderService serviceOrderService;

    public UpdateOrderStatusListener(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @EventListener
    @Async
    public void handle(UpdateOrderStatusEvent event) {
        ServiceOrder order = serviceOrderService.findByUUID(event.orderId());
        order.setUpdateStatus(event.status());
    }
}
