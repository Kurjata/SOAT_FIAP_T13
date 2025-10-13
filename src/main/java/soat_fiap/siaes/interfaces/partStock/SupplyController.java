package soat_fiap.siaes.interfaces.partStock;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.partStock.model.Supply;
import soat_fiap.siaes.domain.partStock.service.SupplyService;
import soat_fiap.siaes.interfaces.partStock.dto.*;

import java.util.UUID;

@RestController
@RequestMapping("/supply-controller")
public class SupplyController {

    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @PostMapping
    public ResponseEntity<Supply> save(@RequestBody @Valid CreateSupplyRequest request) {
        return ResponseEntity.ok().body(supplyService.save(request.toModel()));
    }

    @GetMapping
    public ResponseEntity<Page<SupplyResponse>> findAll(@ParameterObject Pageable pageable) {
        Page<SupplyResponse> response = supplyService.findAll(pageable)
                .map(SupplyResponse::fromModelSupply);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyResponse> findById(@PathVariable UUID id) {
        Supply supply = supplyService.findById(id);
        return ResponseEntity.ok(SupplyResponse.fromModelSupply(supply));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplyResponse> update(@PathVariable UUID id,
                                                 @RequestBody @Valid UpdateSupplyRequest request) {
        Supply updated = supplyService.update(id, request);
        return ResponseEntity.ok(SupplyResponse.fromModelSupply(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supplyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<SupplyResponse> updateAvailability(
            @PathVariable UUID id,
            @RequestBody UpdateSupplyAvailableRequest request) {

        Supply updatedSupply =  supplyService.updateAvailability(id, request.available());
        return ResponseEntity.ok(SupplyResponse.fromModelSupply(updatedSupply));
    }




}
