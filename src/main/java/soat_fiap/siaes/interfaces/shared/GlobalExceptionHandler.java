package soat_fiap.siaes.interfaces.shared;

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
import soat_fiap.siaes.interfaces.user.dto.MessageDTO;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageDTO> handleBadCredentials(BadCredentialsException ex) {
        logger.warn("Tentativa de login inválida: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageDTO("Usuário ou senha inválidos"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageDTO> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Acesso negado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageDTO("Acesso negado: você não possui permissão para acessar este recurso"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageDTO> handleEntityNotFound(EntityNotFoundException ex) {
        logger.info("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageDTO(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageDTO> handleIllegalArgument(IllegalArgumentException ex) {
        logger.info("Argumento inválido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageDTO(ex.getMessage()));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<MessageDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Erro de integridade de dados: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageDTO("Operação inválida: conflito com dados existentes."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDTO> handleGenericException(Exception ex) {
        logger.error("Erro inesperado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageDTO("Ocorreu um erro inesperado. Tente novamente mais tarde."));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}
