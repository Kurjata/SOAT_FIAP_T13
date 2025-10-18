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
@Tag(name = "Order Item")
public class OrderItemController {
    private final ActivityItemService service;

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ActivityItemResponse>> getByItem(@PathVariable UUID itemId) {
        return ResponseEntity.ok(service.findByServiceOrderItem(itemId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityItemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ActivityItemResponse> create(@RequestBody ActivityItemRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityItemResponse> update(
            @PathVariable UUID id,
            @RequestBody ActivityItemRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
