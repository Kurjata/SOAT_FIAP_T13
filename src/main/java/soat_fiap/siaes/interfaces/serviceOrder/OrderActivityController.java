package soat_fiap.siaes.interfaces.serviceOrder;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceOrder.service.OrderActivityService;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.AddOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.OrderActivityResponse;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-activities")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
@Tag(name = "Order Activity")
public class OrderActivityController {
    private final OrderActivityService service;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderActivityResponse>> getByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(service.findByServiceOrder(orderId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderActivityResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderActivityResponse> create(@RequestBody @Valid AddOrderActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderActivityResponse> update(@PathVariable UUID id, @RequestBody @Valid AddOrderActivityRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
