package soat_fiap.siaes.interfaces.serviceOrder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrder.service.OrderActivityService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityResponse;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/service-order-items")
@RequiredArgsConstructor
@Tag(name = "Order Activity")
public class OrderActivityController {
    private final OrderActivityService service;

    //Listar itens de uma ordem
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderActivityResponse>> getByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(service.findByServiceOrder(orderId));
    }

    //Consultar item específico
    @GetMapping("/{id}")
    public ResponseEntity<OrderActivityResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    //Criar item (com insumos opcionais)
    @PostMapping
    public ResponseEntity<OrderActivityResponse> create(@RequestBody @Valid OrderActivityRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    //Alterar item
    @PutMapping("/{id}")
    public ResponseEntity<OrderActivityResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid OrderActivityRequest request
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
