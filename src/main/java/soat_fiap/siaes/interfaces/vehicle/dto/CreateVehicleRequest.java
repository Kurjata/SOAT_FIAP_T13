package soat_fiap.siaes.interfaces.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

public record CreateVehicleRequest(
        @NotBlank String plate,
        @NotBlank String brand,
        @NotBlank String model,
        @NotNull Integer year
) {
    public Vehicle toModel() {
        return new Vehicle(plate, brand, model, year);
    }
}