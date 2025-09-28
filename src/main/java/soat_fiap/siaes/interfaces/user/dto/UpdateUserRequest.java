package soat_fiap.siaes.interfaces.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.domain.user.model.RoleEnum;

public record UpdateUserRequest(
        @NotBlank
        String name,
        @NotBlank
        String document,
        @NotNull
        RoleEnum role
) {}