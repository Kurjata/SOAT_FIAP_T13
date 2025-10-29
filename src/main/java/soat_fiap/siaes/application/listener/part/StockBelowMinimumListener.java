package soat_fiap.siaes.application.listener.part;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import soat_fiap.siaes.application.event.part.StockBelowMinimumEvent;

@Slf4j
@Component
public class StockBelowMinimumListener {

    @EventListener
    public void handle(StockBelowMinimumEvent event) {
        log.warn(
                "[EMAIL SIMULADO] Estoque abaixo do mínimo para a peça '{}' (ID: {}). Quantidade atual: {}. Quantidade mínima: {}.",
                event.name(),
                event.id(),
                event.quantity(),
                event.minimumStockQuantity()
        );
    }
}