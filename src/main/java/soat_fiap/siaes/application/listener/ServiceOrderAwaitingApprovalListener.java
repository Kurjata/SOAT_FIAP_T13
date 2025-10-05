package soat_fiap.siaes.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import soat_fiap.siaes.application.event.ServiceOrder.ServiceOrderAwaitingApprovalEvent;
import soat_fiap.siaes.domain.serviceOrderToken.service.ServiceOrderClientService;

@Component
@RequiredArgsConstructor
public class ServiceOrderAwaitingApprovalListener {

    private final ServiceOrderClientService clientService;

    @Async
    @EventListener
    public void handle(ServiceOrderAwaitingApprovalEvent event) {
        clientService.sendApprovalLink(event.serviceOrder());
    }
}
