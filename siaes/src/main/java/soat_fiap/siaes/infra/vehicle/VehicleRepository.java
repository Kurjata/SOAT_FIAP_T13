package soat_fiap.siaes.infra.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import soat_fiap.siaes.domain.vehicle.Vehicle;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
}