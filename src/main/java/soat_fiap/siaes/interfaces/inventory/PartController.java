package soat_fiap.siaes.interfaces.inventory;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.service.PartService;
import soat_fiap.siaes.interfaces.inventory.dto.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/parts-controller")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Parts")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ResponseEntity<Part> save(@RequestBody @Valid CreatePartRequest request) {
        return ResponseEntity.ok().body(partService.save(request.toModel())) ;
    }

    @GetMapping
    public ResponseEntity<Page<PartResponse>> findAll(@ParameterObject Pageable pageable) {
        Page<PartResponse> response = partService.findAll(pageable)
                .map(PartResponse::fromModel);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> findById(@PathVariable UUID id) {
        Part part = partService.findById(id);
        return ResponseEntity.ok(PartResponse.fromModel(part));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> update(@PathVariable UUID id,
                                               @RequestBody @Valid UpdatePartRequest request) {
        Part updated = partService.update(id, request);
        return ResponseEntity.ok(PartResponse.fromModel(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        partService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    //simular a entrada de peça do fornecedor
    @PatchMapping("/{id}/addstock")
    public ResponseEntity<PartResponse> addStock(
            @PathVariable UUID id,
            @RequestBody AddStockRequest  request) {

        Part updatedPart = partService.addStock(id, request.quantity());
        return ResponseEntity.ok(PartResponse.fromModel(updatedPart));

    }

    //Ajuste de estoque fisico para virtual
    // número positivo = adiciona, número negativo = reduz
    @PostMapping("/adjust-stock/{id}")
    public ResponseEntity<PartResponse> adjustStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity
    ) {
        Part part = partService.findById(id);
        Part updated = partService.updateStockQuantity(id, quantity);
        return ResponseEntity.ok(PartResponse.fromModel(updated));
    }

    @GetMapping("/below-minimum-stock")
    public ResponseEntity<List<PartResponse>> findAllBelowMinimumStock() {
        List<PartResponse> response = partService.findPartsBelowMinimumStock()
                .stream()
                .map(PartResponse::fromModel)
                .toList();

        return ResponseEntity.ok(response);
    }

}
