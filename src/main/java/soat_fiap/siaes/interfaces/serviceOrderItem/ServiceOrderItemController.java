package soat_fiap.siaes.interfaces.serviceOrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrderItem.service.ServiceOrderItemService;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.ServiceOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.ServiceOrderItemResponse;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/service-order-items")
@RequiredArgsConstructor
public class ServiceOrderItemController {
    private final ServiceOrderItemService service;

    //Listar itens de uma ordem
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ServiceOrderItemResponse>> getByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(service.findByServiceOrder(orderId));
    }

    //Consultar item específico
    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderItemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    //Criar item (com insumos opcionais)
    @PostMapping
    public ResponseEntity<ServiceOrderItemResponse> create(@RequestBody @Valid ServiceOrderItemRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    //Alterar item
    @PutMapping("/{id}")
    public ResponseEntity<ServiceOrderItemResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid ServiceOrderItemRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    //Excluir item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
