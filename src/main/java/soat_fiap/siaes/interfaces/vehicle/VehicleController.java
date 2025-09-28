package soat_fiap.siaes.interfaces.vehicle;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.interfaces.vehicle.dto.CreateVehicleRequest;
import soat_fiap.siaes.interfaces.vehicle.dto.UpdateVehicleRequest;
import soat_fiap.siaes.interfaces.vehicle.dto.VehicleResponse;
import soat_fiap.siaes.domain.vehicle.service.VehicleService;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@SecurityRequirement(name = "bearer-key")
@AllArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> findAll(@ParameterObject Pageable pageable) {
        Page<VehicleResponse> vehicles = vehicleService.findAll(pageable)
                .map(VehicleResponse::new);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id) {
        Vehicle vehicle = vehicleService.findById(id);
        return ResponseEntity.ok(new VehicleResponse(vehicle));
    }

    @PostMapping
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
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id,
                                                  @RequestBody @Valid UpdateVehicleRequest request) {
        Vehicle updatedVehicle = vehicleService.update(id, request);
        return ResponseEntity.ok(new VehicleResponse(updatedVehicle));
    }
}