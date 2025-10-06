package soat_fiap.siaes.interfaces.user.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.interfaces.shared.Document;

public record CreateUserRequest(
        @NotBlank String name,
        @NotBlank String login,
        @NotBlank String password,
        @Document String document
) {
    public User toModel(PasswordEncoder encoder) {
        return new User(name, login, encoder.encode(password), RoleEnum.CLIENT, document);
    }
}