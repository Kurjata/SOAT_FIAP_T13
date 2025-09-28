package soat_fiap.siaes.interfaces.user.dto;

import soat_fiap.siaes.domain.user.model.RoleEnum;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String username,
        RoleEnum roleEnum
) {
}
