package soat_fiap.siaes.interfaces.vehicle.dto;

import soat_fiap.siaes.domain.vehicle.model.Vehicle;

public record VehicleResponse(
        String id,
        String plate,
        String brand,
        String model,
        int year
) {
    public VehicleResponse(Vehicle vehicle) {
        this(vehicle.getIdAsString(), vehicle.getPlate(), vehicle.getBrand(), vehicle.getModel(), vehicle.getYear());
    }
}