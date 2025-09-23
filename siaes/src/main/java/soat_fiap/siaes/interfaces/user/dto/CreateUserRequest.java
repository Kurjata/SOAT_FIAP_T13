package soat_fiap.siaes.interfaces.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.shared.Document;

public record CreateUserRequest(
        @NotBlank String name,
        @NotBlank String login,
        @NotBlank String password,
        @NotNull RoleEnum role,
        @Document String document
) {
    public User toModel(PasswordEncoder encoder) {
        return new User(name, login, encoder.encode(password), role, document);
    }
}