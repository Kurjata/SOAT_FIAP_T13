package soat_fiap.siaes.interfaces.vehicle;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.interfaces.vehicle.dto.CreateVehicleRequest;
import soat_fiap.siaes.interfaces.vehicle.dto.UpdateVehicleRequest;
import soat_fiap.siaes.interfaces.vehicle.dto.VehicleResponse;
import soat_fiap.siaes.domain.vehicle.service.VehicleService;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> findAll() {
        List<VehicleResponse> vehicles = vehicleService.findAll()
                .stream()
                .map(VehicleResponse::new)
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id) {
        Vehicle vehicle = vehicleService.findById(id);
        return ResponseEntity.ok(new VehicleResponse(vehicle));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<VehicleResponse> save(@RequestBody @Valid CreateVehicleRequest vehicleRequest) {
        Vehicle vehicle = this.vehicleService.save(vehicleRequest.toModel());
        return ResponseEntity.ok(new VehicleResponse(vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id,
                                                  @RequestBody @Valid UpdateVehicleRequest request) {
        Vehicle updatedVehicle = vehicleService.update(id, request);
        return ResponseEntity.ok(new VehicleResponse(updatedVehicle));
    }
}