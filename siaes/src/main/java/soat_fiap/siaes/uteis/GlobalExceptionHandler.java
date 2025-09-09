package soat_fiap.siaes.uteis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import soat_fiap.siaes.rest.dto.MessageDTO;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata erros de validação de campos (@Valid, @NotBlank, etc.).
     *
     * @param ex exceção lançada pelo Spring durante a validação
     * @return ResponseEntity com mapa de campos e mensagens de erro
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Trata tentativas de login com credenciais inválidas.
     *
     * @param ex BadCredentialsException lançada pelo Spring Security
     * @return ResponseEntity com mensagem de erro
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageDTO> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Tentativa de login inválida: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageDTO("Usuário ou senha inválidos"));
    }

    /**
     * Trata acesso negado (HTTP 403).
     *
     * @param ex AccessDeniedException lançada pelo Spring Security
     * @return ResponseEntity com mensagem de erro
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageDTO> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Acesso negado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageDTO("Acesso negado: você não possui permissão para acessar este recurso"));
    }

    /**
     * Trata recurso não encontrado (HTTP 404).
     *
     * @param ex EntityNotFoundException lançada ao tentar buscar entidade inexistente
     * @return ResponseEntity com mensagem de erro
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageDTO> handleEntityNotFound(EntityNotFoundException ex) {
        logger.info("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageDTO(ex.getMessage()));
    }

    /**
     * Trata argumentos inválidos (HTTP 400).
     *
     * @param ex IllegalArgumentException lançada quando um argumento inválido é fornecido
     * @return ResponseEntity com mensagem de erro
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageDTO> handleIllegalArgument(IllegalArgumentException ex) {
        logger.info("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageDTO(ex.getMessage()));
    }

    /**
     * Trata erros de integridade de dados (HTTP 409).
     *
     * @param ex DataIntegrityViolationException lançada pelo Spring Data
     * @return ResponseEntity com mensagem de conflito
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Erro de integridade de dados: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageDTO("Operação inválida: conflito com dados existentes."));
    }


    /**
     * Trata quaisquer erros genéricos (HTTP 500).
     *
     * @param ex Exception genérica
     * @return ResponseEntity com mensagem de erro inesperado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDTO> handleGenericException(Exception ex) {
        logger.error("Erro inesperado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageDTO("Ocorreu um erro inesperado. Tente novamente mais tarde."));
    }
}
