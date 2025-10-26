package soat_fiap.siaes.interfaces.serviceOrder;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrder.service.OrderItemService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.OrderItemResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-items")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
@Tag(name = "Order Item")
public class OrderItemController {
    private final OrderItemService service;

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<OrderItemResponse>> getByOrderActivity(@PathVariable UUID itemId) {
        return ResponseEntity.ok(service.findAllByOrderActivity(itemId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> create(@RequestBody AddOrderItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemResponse> update(@PathVariable UUID id, @RequestBody CreateOrderItemRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
