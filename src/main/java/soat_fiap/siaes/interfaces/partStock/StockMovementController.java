package soat_fiap.siaes.interfaces.partStock;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import soat_fiap.siaes.domain.partStock.service.StockMovementService;
import soat_fiap.siaes.interfaces.partStock.dto.StockMovementResponse;

import java.util.UUID;

@RestController
@RequestMapping("/stock-movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;

    @GetMapping
    public ResponseEntity<Page<StockMovementResponse>> findAll(Pageable pageable) {
        Page<StockMovementResponse> movements = stockMovementService.findAll(pageable)
                .map(StockMovementResponse::new);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/part/{partId}")
    public ResponseEntity<Page<StockMovementResponse>> findByPart(
            @PathVariable UUID partId,
            Pageable pageable
    ) {
        Page<StockMovementResponse> movements = stockMovementService.findByPart(partId, pageable)
                .map(StockMovementResponse::new);
        return ResponseEntity.ok(movements);
    }
}