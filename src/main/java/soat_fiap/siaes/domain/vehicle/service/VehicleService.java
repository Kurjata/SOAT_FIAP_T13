package soat_fiap.siaes.domain.vehicle.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.vehicle.repository.VehicleRepository;
import soat_fiap.siaes.interfaces.vehicle.dto.UpdateVehicleRequest;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.util.UUID;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Page<Vehicle> findAll(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    public Vehicle findById(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));
    }

    public Vehicle update(UUID id, UpdateVehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + id));

        existing.setPlate(request.plate());
        existing.setBrand(request.brand());
        existing.setModel(request.model());
        existing.setYear(request.year());

        return vehicleRepository.save(existing);
    }

    public Vehicle save(Vehicle vehicle) {
        if (vehicleRepository.existsByPlate((vehicle.getPlate())))
            throw new IllegalArgumentException("plate already exists");

        return vehicleRepository.save(vehicle);
    }

    public void deleteById(UUID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new EntityNotFoundException("Vehicle not found with id: " + id);
        }

        vehicleRepository.deleteById(id);
    }
}