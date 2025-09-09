package soat_fiap.siaes.infrastructure.persistence.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;

import java.util.UUID;

public interface VehicleJpaRepository extends JpaRepository<Vehicle, UUID>, VehicleRepository {
}