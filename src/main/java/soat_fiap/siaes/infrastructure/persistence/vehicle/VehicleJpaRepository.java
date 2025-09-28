package soat_fiap.siaes.infrastructure.persistence.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;

import java.util.UUID;

@Repository
public interface VehicleJpaRepository extends JpaRepository<Vehicle, UUID>, VehicleRepository {
}