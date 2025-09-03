package soat_fiap.siaes.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(
        @NotBlank(message = "O refresh token não pode estar vazio")
        String refreshToken
) {
}
