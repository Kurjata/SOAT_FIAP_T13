package soat_fiap.siaes.interfaces.serviceOrder;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrder.service.ServiceOrderService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ServiceOrderResponse;

import java.util.UUID;

@RestController
@RequestMapping("/service-orders")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class ServiceOrderController {
    private final ServiceOrderService service;

    //Busca todos
    @GetMapping
    public ResponseEntity<Page<ServiceOrderResponse>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    // Consultar ordem por ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Consultar por documento do usuário
    @GetMapping("/user/document/{cpfCnpj}")
    public ResponseEntity<Page<ServiceOrderResponse>> getByUserDocument(
            @PathVariable String cpfCnpj, Pageable pageable) {
        return ResponseEntity.ok(service.findByUserDocument(cpfCnpj, pageable));
    }

    // Consultar por ID do usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ServiceOrderResponse>> findByUser(
            @PathVariable UUID userId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findByUserId(userId, pageable));
    }

    // Consultar por ID do veículo
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Page<ServiceOrderResponse>> getByVehicleId(
            @PathVariable UUID vehicleId, Pageable pageable) {
        return ResponseEntity.ok(service.findByVehicleId(vehicleId, pageable));
    }

    // Consultar por placa do veículo
    @GetMapping("/vehicle/plate/{plate}")
    public ResponseEntity<Page<ServiceOrderResponse>> getByVehiclePlate(
            @PathVariable String plate, Pageable pageable) {
        return ResponseEntity.ok(service.findByVehiclePlate(plate, pageable));
    }

    // Criar ordem de serviço
    @PostMapping
    public ResponseEntity<ServiceOrderResponse> create(@RequestBody ServiceOrderRequest request) {
        return ResponseEntity.ok(service.createServiceOrder(request));
    }

    // Atualizar status da ordem
    @PatchMapping("/client/status/{id}")
    public ResponseEntity<ServiceOrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam ServiceOrderStatusEnum status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    // Excluir ordem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    //Busca by usuário logado
    @GetMapping("/client/me")
    public ResponseEntity<Page<ServiceOrderResponse>> findAllMe(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAllMe(pageable));
    }
}
