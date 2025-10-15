package soat_fiap.siaes.interfaces.serviceOrder;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrder.service.ActivityItemService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ActivityItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ActivityItemResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/service-order-item-supplies")
@RequiredArgsConstructor
@Tag(name = "Activity Item")
public class ActivityItemController {
    private final ActivityItemService service;

    //Listar todos os insumos de um item de ordem de serviço
    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ActivityItemResponse>> getByItem(@PathVariable UUID itemId) {
        return ResponseEntity.ok(service.findByServiceOrderItem(itemId));
    }

    // Consultar insumo específico
    @GetMapping("/{id}")
    public ResponseEntity<ActivityItemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Criar insumo
    @PostMapping
    public ResponseEntity<ActivityItemResponse> create(@RequestBody ActivityItemRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    // Alterar insumo (quantidade ou preço)
    @PutMapping("/{id}")
    public ResponseEntity<ActivityItemResponse> update(
            @PathVariable UUID id,
            @RequestBody ActivityItemRequest request
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
