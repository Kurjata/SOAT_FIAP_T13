package soat_fiap.siaes.domain.vehicle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(UUID id);
    Page<Vehicle> findAll(Pageable pageable);
    void deleteById(UUID id);
    boolean existsByPlate(String plate);
    boolean existsById(UUID id);
}
