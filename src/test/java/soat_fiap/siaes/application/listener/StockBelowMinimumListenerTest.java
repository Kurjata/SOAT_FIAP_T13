package soat_fiap.siaes.application.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic; // Importe este
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Importe este
import soat_fiap.siaes.application.event.part.StockBelowMinimumEvent;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockBelowMinimumListenerTest {
    // Mock do logger SLF4J
    @Mock
    private Logger mockLogger;

    // Instanciamos o listener dentro do try-with-resources
    private final UUID TEST_ID = UUID.randomUUID();
    private final String TEST_NAME = "Filtro de Óleo";
    private final int CURRENT_QUANTITY = 5;
    private final int MINIMUM_QUANTITY = 10;

    @Test
    void handle_should_log_warning_message_with_correct_details() {
        // ARRANGE

        // Usa Mockito.mockStatic para interceptar a chamada a LoggerFactory.getLogger()
        try (MockedStatic<LoggerFactory> mockedStatic = mockStatic(LoggerFactory.class)) {

            // Garante que qualquer chamada a getLogger retorne nosso mock
            mockedStatic.when(() -> LoggerFactory.getLogger(StockBelowMinimumListener.class))
                    .thenReturn(mockLogger);

            // Instancia o listener AGORA, DENTRO DO ESCOPO MOCKADO!
            StockBelowMinimumListener listener = new StockBelowMinimumListener();

            StockBelowMinimumEvent event = new StockBelowMinimumEvent(
                    TEST_ID, TEST_NAME, CURRENT_QUANTITY, MINIMUM_QUANTITY
            );

            // A LINHA 'when(mockLogger.isWarnEnabled()).thenReturn(true);' FOI REMOVIDA AQUI.

            // Captura a string de formato e os argumentos
            ArgumentCaptor<String> messageFormatCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Object> argsCaptor = ArgumentCaptor.forClass(Object.class);

            // ACT
            listener.handle(event);

            // ASSERT
            // Verifica se o log.warn foi chamado com a string de formato e 4 argumentos
            verify(mockLogger).warn(
                    messageFormatCaptor.capture(),
                    argsCaptor.capture(),
                    argsCaptor.capture(),
                    argsCaptor.capture(),
                    argsCaptor.capture()
            );

            // Verifica os argumentos
            List<Object> capturedArgs = argsCaptor.getAllValues();
            assertThat(capturedArgs.get(0)).isEqualTo(TEST_NAME);
            assertThat(capturedArgs.get(1)).isEqualTo(TEST_ID.toString());
            assertThat(capturedArgs.get(2)).isEqualTo(CURRENT_QUANTITY);
            assertThat(capturedArgs.get(3)).isEqualTo(MINIMUM_QUANTITY);

        } // O mock estático é liberado aqui
    }
}

