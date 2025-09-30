package soat_fiap.siaes.domain.partStock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.partStock.model.MovementType;
import soat_fiap.siaes.domain.partStock.model.PartStock;
import soat_fiap.siaes.domain.partStock.model.StockMovement;
import soat_fiap.siaes.domain.partStock.repository.PartStockRepository;
import soat_fiap.siaes.domain.partStock.repository.StockMovementRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final PartStockRepository partRepository;

    @Transactional
    public StockMovement registerMovement(
            UUID partId,
            MovementType type,
            Integer quantity,
            UUID orderId
    ) {
        PartStock part = partRepository.findById(partId)
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada com ID: " + partId));

        int balanceAfter = part.isStockControl() ? part.getStockQuantity() : 0;

        BigDecimal unitPrice = part.getUnitPrice() != null
                ? BigDecimal.valueOf(part.getUnitPrice())
                : BigDecimal.ZERO;

        BigDecimal totalValue = unitPrice.multiply(BigDecimal.valueOf(quantity));

        StockMovement movement = StockMovement.builder()
                .part(part)
                .type(type)
                .quantity(quantity)
                .balanceAfter(balanceAfter)
                .unitPrice(unitPrice)
                .totalValue(totalValue)
                .orderId(orderId)
                .build();

        return stockMovementRepository.save(movement);
    }

    @Transactional(readOnly = true)
    public Page<StockMovement> findAll(Pageable pageable) {
        return stockMovementRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<StockMovement> findByPart(UUID partId, Pageable pageable) {
        return stockMovementRepository.findByPartId(partId, pageable);
    }
}
