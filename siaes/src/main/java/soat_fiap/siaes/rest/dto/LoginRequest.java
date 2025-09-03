package soat_fiap.siaes.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O login não pode estar vazio")
        String login,

        @NotBlank(message = "A senha não pode estar vazia")
        String password) {
}
