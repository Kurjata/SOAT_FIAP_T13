package soat_fiap.siaes.rest.dto;

import soat_fiap.siaes.domain.enums.RoleEnum;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String username,
        RoleEnum roleEnum
) {
}
