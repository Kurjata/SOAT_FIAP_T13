package soat_fiap.siaes.interfaces.serviceOrder;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.serviceOrder.service.ServiceOrderService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;

import java.util.UUID;

@RestController
@RequestMapping("/service-orders")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
@Tag(name = "Service Order")
public class ServiceOrderController {
    private final ServiceOrderService service;

    @GetMapping
    public ResponseEntity<Page<ServiceOrderResponse>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/user/document/{cpfCnpj}")
    public ResponseEntity<Page<ServiceOrderResponse>> getByUserDocument(
            @PathVariable String cpfCnpj, Pageable pageable) {
        return ResponseEntity.ok(service.findByUserDocument(cpfCnpj, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ServiceOrderResponse>> findByUser(
            @PathVariable UUID userId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findByUserId(userId, pageable));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Page<ServiceOrderResponse>> getByVehicleId(
            @PathVariable UUID vehicleId, Pageable pageable) {
        return ResponseEntity.ok(service.findByVehicleId(vehicleId, pageable));
    }

    @GetMapping("/vehicle/plate/{plate}")
    public ResponseEntity<Page<ServiceOrderResponse>> getByVehiclePlate(
            @PathVariable String plate, Pageable pageable) {
        return ResponseEntity.ok(service.findByVehiclePlate(plate, pageable));
    }

    @PostMapping
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(service.createServiceOrder(request));
    }

    @PatchMapping("/client/status/{id}")
    public ResponseEntity<ServiceOrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam ServiceOrderStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/me")
    public ResponseEntity<Page<ServiceOrderResponse>> findAllMe(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAllMe(pageable));
    }
}
