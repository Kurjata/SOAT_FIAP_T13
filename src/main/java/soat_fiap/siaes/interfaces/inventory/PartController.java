package soat_fiap.siaes.interfaces.inventory;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.service.PartService;
import soat_fiap.siaes.interfaces.inventory.dto.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/parts")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ResponseEntity<Part> save(@RequestBody @Valid CreatePartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partService.save(request.toModel())) ;
    }

    @GetMapping
    public ResponseEntity<Page<PartResponse>> findAll(@ParameterObject Pageable pageable) {
        Page<PartResponse> response = partService.findAll(pageable)
                .map(PartResponse::new);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> findById(@PathVariable UUID id) {
        Part part = partService.findById(id);
        return ResponseEntity.ok(new PartResponse(part));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> update(@PathVariable UUID id,
                                               @RequestBody @Valid UpdatePartRequest request) {
        Part updated = partService.update(id, request);
        return ResponseEntity.ok(new PartResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        partService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock/add")
    public ResponseEntity<PartResponse> addStock(
            @PathVariable UUID id,
            @RequestBody AddStockRequest  request) {

        Part updatedPart = partService.addStock(id, request.quantity());
        return ResponseEntity.ok(new PartResponse(updatedPart));
    }

    @PatchMapping("/{id}/stock/adjust")
    public ResponseEntity<PartResponse> adjustStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity
    ) {
        Part updated = partService.updateStockQuantity(id, quantity);
        return ResponseEntity.ok(new PartResponse(updated));
    }

    @GetMapping("/stock/below-minimum")
    public ResponseEntity<List<PartResponse>> findAllBelowMinimumStock() {
        List<PartResponse> response = partService.findPartsBelowMinimumStock()
                .stream()
                .map(PartResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

}
