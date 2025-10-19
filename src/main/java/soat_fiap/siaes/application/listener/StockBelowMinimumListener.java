package soat_fiap.siaes.application.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import soat_fiap.siaes.application.event.part.StockBelowMinimumEvent;

@Component
public class StockBelowMinimumListener {

    @EventListener
    public void handle(StockBelowMinimumEvent event) {
        System.out.printf(
                "[EMAIL SIMULADO] Estoque abaixo do mínimo para a peça '%s' (ID: %s). Quantidade atual: %d. Quantidade mínima: %d.%n",
                event.name(),
                event.id().toString(),
                event.quantity(),
                event.minimumStockQuantity()
        );
    }
}