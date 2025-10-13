package soat_fiap.siaes.interfaces.partStock;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import soat_fiap.siaes.domain.partStock.service.StockMovementService;
import soat_fiap.siaes.interfaces.partStock.dto.StockMovementResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stock-movements")
@SecurityRequirement(name = "bearer-key")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }


    @GetMapping
    public ResponseEntity<Page<StockMovementResponse>> findAll(@ParameterObject Pageable pageable) {
        Page<StockMovementResponse> movements = stockMovementService.findAll(pageable);
        return ResponseEntity.ok(movements);
    }


    @GetMapping("/movementpart/{partId}")
    public ResponseEntity<Page<StockMovementResponse>> findByPart(@PathVariable UUID partId,@ParameterObject Pageable pageable
    ) {
        Page<StockMovementResponse> movements = stockMovementService.findByPart(partId, pageable);
        return ResponseEntity.ok(movements);
    }
}
