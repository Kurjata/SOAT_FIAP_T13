package soat_fiap.siaes.domain.partStock.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import soat_fiap.siaes.domain.partStock.event.StockBelowMinimumEvent;

public class StockBelowMinimumListener {

    private static final Logger log = LoggerFactory.getLogger(StockBelowMinimumListener.class);

    @EventListener
    public void handleStockBelowMinimum(StockBelowMinimumEvent event) {
        log.warn("ALERTA: {}", event.getMessage());

        // lembrar de pensar de criar regra de envio de e-mail ou log
    }
}
