package soat_fiap.siaes.interfaces.partStock;


import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.service.PartService;
import soat_fiap.siaes.interfaces.partStock.dto.*;

import java.util.UUID;


@RestController
@RequestMapping("/parts-controller")
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

}
