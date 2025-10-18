package soat_fiap.siaes.domain.vehicle.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.interfaces.vehicle.dto.UpdateVehicleRequest;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.util.UUID;

@Service
@AllArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    public Vehicle findById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Veículo não encontrado com id: " + id));
    }

    @Transactional
    public Vehicle save(Vehicle vehicle) {
        if (vehicleRepository.existsByPlate(vehicle.getPlate())) {
            throw new IllegalArgumentException("A placa '" + vehicle.getPlate() + "' já está em uso.");
        }

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle update(UUID id, UpdateVehicleRequest request) {
        if (vehicleRepository.existsByPlateAndIdNot(request.plate(), id)) {
            throw new IllegalArgumentException(
                    "A placa '" + request.plate() + "' já está em uso por outro veículo.");
        }

        Vehicle existing = findById(id);
        existing.setPlate(request.plate());
        existing.setBrand(request.brand());
        existing.setModel(request.model());
        existing.setYear(request.year());

        return vehicleRepository.save(existing);
    }

    public void deleteById(UUID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new EntityNotFoundException("Veículo não encontrado com id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public Vehicle findByPlateIgnoreCase(String vehiclePlate){
        return vehicleRepository.findByPlateIgnoreCase(vehiclePlate)
                .orElseThrow(() -> new EntityNotFoundException("Veículo com placa " + vehiclePlate + " não encontrado"));
    }
}
