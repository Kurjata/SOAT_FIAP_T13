package soat_fiap.siaes.interfaces.serviceOrderItemSupply;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrderItemSupply.service.ServiceOrderItemSupplyService;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyRequest;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/service-order-item-supplies")
@RequiredArgsConstructor
public class ServiceOrderItemSupplyController {
    private final ServiceOrderItemSupplyService service;

    //Listar todos os insumos de um item de ordem de serviço
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ServiceOrderItemSupplyResponse>> getByItem(@PathVariable UUID itemId) {
        return ResponseEntity.ok(service.findByServiceOrderItem(itemId));
    }

    // Consultar insumo específico
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderItemSupplyResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Criar insumo
    @PostMapping
    public ResponseEntity<ServiceOrderItemSupplyResponse> create(@RequestBody ServiceOrderItemSupplyRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    // Alterar insumo (quantidade ou preço)
    @PutMapping("/{id}")
    public ResponseEntity<ServiceOrderItemSupplyResponse> update(
            @PathVariable UUID id,
            @RequestBody ServiceOrderItemSupplyRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    //Excluir insumo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
