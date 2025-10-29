package soat_fiap.siaes.application.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import soat_fiap.siaes.application.event.part.StockBelowMinimumEvent;
import soat_fiap.siaes.application.listener.part.StockBelowMinimumListener;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StockBelowMinimumListenerTest {

    @Test
    void handle_should_log_warning_message_with_correct_details() {
        Logger logger = (Logger) LoggerFactory.getLogger(StockBelowMinimumListener.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        StockBelowMinimumListener listener = new StockBelowMinimumListener();
        StockBelowMinimumEvent event = new StockBelowMinimumEvent(UUID.randomUUID(), "Peça X", 3, 10);

        listener.handle(event);

        List<ILoggingEvent> logs = listAppender.list;
        assertEquals(1, logs.size(), "Deveria haver exatamente um log registrado.");

        ILoggingEvent logEvent = logs.get(0);
        assertEquals(Level.WARN, logEvent.getLevel(), "O nível do log deveria ser WARN.");

        String message = logEvent.getFormattedMessage();
        assertTrue(message.contains("Estoque abaixo do mínimo"), "Mensagem deveria conter 'Estoque abaixo do mínimo'");
        assertTrue(message.contains("Peça X"), "Mensagem deveria conter o nome da peça");
        assertTrue(message.contains("Quantidade atual: 3"), "Mensagem deveria conter a quantidade atual");
        assertTrue(message.contains("Quantidade mínima: 10"), "Mensagem deveria conter a quantidade mínima");

        listAppender.stop();
    }
}
