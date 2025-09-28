package soat_fiap.siaes.interfaces.partStock;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class PartStockController {

    private final PartStockService service;

    @GetMapping
    public ResponseEntity<Page<PartStockResponse>> findAll(Pageable pageable) {
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
}
