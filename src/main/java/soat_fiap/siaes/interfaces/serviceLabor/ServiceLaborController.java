package soat_fiap.siaes.interfaces.serviceLabor;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborRequest;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;

import java.util.UUID;


@RestController
@RequestMapping("/service-labor")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
@Tag(name = "Service Labor")
public class ServiceLaborController {
    private final ServiceLaborService service;

    @GetMapping
    public ResponseEntity<Page<ServiceLaborResponse>> findAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceLaborResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceLaborResponse> save(@RequestBody @Valid ServiceLaborRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceLaborResponse> update(@PathVariable UUID id, @RequestBody @Valid ServiceLaborRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
