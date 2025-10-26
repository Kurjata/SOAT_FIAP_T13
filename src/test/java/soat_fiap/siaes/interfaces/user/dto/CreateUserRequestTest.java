package soat_fiap.siaes.interfaces.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateUserRequestTest {

    private Validator validator;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode("123456")).thenReturn("encoded123456");
    }

    @Test
    void should_create_valid_request() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "123456", "384.088.920-02", "vinelouzada@gmail.com");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void should_fail_when_name_is_blank() {
        CreateUserRequest request = new CreateUserRequest("", "vinelouzada", "123456", "384.088.920-02", "vinelouzada@gmail.com");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("não deve estar em branco", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_login_is_blank() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "", "123456", "384.088.920-02", "vinelouzada@gmail.com");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("não deve estar em branco", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_password_is_blank() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "", "384.088.920-02", "vinelouzada@gmail.com");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("não deve estar em branco", violations.iterator().next().getMessage());
    }

    @Test
    void should_fail_when_document_is_invalid() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "123456", "INVALID", "vinelouzada@gmail.com");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123456",
            "111.111.111-11",
            "12.345.678/0001-00",
            "00000000000",
            "12.345.678/0001-9",
            "abcdefghijk"
    })
    void should_fail_when_document_is_invalid_cpf_or_cnpj(String invalidDocument) {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "123456", invalidDocument, "vinelouzada@gmail.com");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .contains("Documento deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos) válido");
    }

    @Test
    void should_fail_when_email_is_blank() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "123456", "384.088.920-02", "");
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("não deve estar em branco", violations.iterator().next().getMessage());
    }

    @Test
    void should_convert_to_model() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "123456", "384.088.920-02", "vinelouzada@gmail.com");
        User user = request.toModel(passwordEncoder);

        assertEquals("Vinicius", user.getName());
        assertEquals("vinelouzada", user.getLogin());
        assertEquals("encoded123456", user.getPassword());
        assertEquals(RoleEnum.CLIENT, user.getRole());
        assertEquals("384.088.920-02", user.getDocumentAsString());
        assertEquals("vinelouzada@gmail.com", user.getEmail());
    }
}