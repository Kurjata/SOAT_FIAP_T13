package soat_fiap.siaes.domain.vehicle.repository;

import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(UUID id);
    List<Vehicle> findAll();
    void deleteById(UUID id);
    boolean existsByPlate(String plate);
    boolean existsById(UUID id);
}
