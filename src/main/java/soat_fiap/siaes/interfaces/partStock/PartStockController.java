package soat_fiap.siaes.interfaces.partStock;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.partStock.model.PartStock;
import soat_fiap.siaes.domain.partStock.service.PartStockService;
import soat_fiap.siaes.interfaces.partStock.dto.CreatePartStockRequest;
import soat_fiap.siaes.interfaces.partStock.dto.PartStockResponse;
import soat_fiap.siaes.interfaces.partStock.dto.UpdatePartStockRequest;


import java.util.UUID;

@RestController
@RequestMapping("/parts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class PartStockController {

    private final PartStockService service;

    @GetMapping
    public ResponseEntity<Page<PartStockResponse>> findAll(@ParameterObject Pageable pageable) {
        Page<PartStockResponse> parts = service.findAll(pageable)
                .map(PartStockResponse::new);
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartStockResponse> findById(@PathVariable UUID id) {
        PartStock part = service.findById(id);
        return ResponseEntity.ok(new PartStockResponse(part));
    }

    @PostMapping
    public ResponseEntity<PartStockResponse> create(@RequestBody @Valid CreatePartStockRequest request) {
        PartStock saved = service.save(request.toPartStock());
        return ResponseEntity.ok(new PartStockResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartStockResponse> update(@PathVariable UUID id,
                                                    @RequestBody @Valid UpdatePartStockRequest request) {
        PartStock updated = service.update(id, request);
        return ResponseEntity.ok(new PartStockResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/consume")
    public ResponseEntity<PartStockResponse> consume(@PathVariable UUID id,
                                                     @RequestParam int quantity,
                                                     @RequestParam(required = false) UUID orderId) {
        PartStock updated = service.consumeStock(id, quantity, orderId);
        return ResponseEntity.ok(new PartStockResponse(updated));
    }

    @PostMapping("/{id}/add-stock")
    public ResponseEntity<PartStockResponse> addStock(@PathVariable UUID id,
                                                      @RequestParam int quantity) {
        PartStock updated = service.addStock(id, quantity);
        return ResponseEntity.ok(new PartStockResponse(updated));
    }


}
