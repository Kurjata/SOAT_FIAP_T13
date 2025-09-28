package soat_fiap.siaes.interfaces.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.shared.validation.BrazilianLicensePlate;

public record UpdateVehicleRequest(
        @NotBlank(message = "A placa é obrigatória")
        @BrazilianLicensePlate
        String plate,

        @NotBlank(message = "A marca é obrigatória")
        String brand,

        @NotBlank(message = "O modelo é obrigatório")
        String model,

        @NotNull(message = "O ano é obrigatório")
        Integer year
) {
}