package soat_fiap.siaes.interfaces.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateVehicleRequest(
        @NotBlank String plate,
        @NotBlank String brand,
        @NotBlank String model,
        @NotNull Integer year
) {
}