package soat_fiap.siaes.domain.partStock.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.partStock.model.MovementType;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.model.StockMovement;
import soat_fiap.siaes.domain.partStock.repository.StockMovementRepository;
import soat_fiap.siaes.interfaces.partStock.dto.StockMovementResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public void registerMovement(Part part, MovementType type, Integer quantity) {
        int before = part.getQuantity() != null ? part.getQuantity() : 0;
        int after = before + quantity;

        BigDecimal unitPrice = part.getUnitPrice() != null ? part.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(Math.abs(quantity)));

        StockMovement movement = StockMovement.builder()
                .part(part)
                .type(type)
                .quantity(quantity)
                .balanceBefore(before)
                .balanceAfter(after)
                .unitPrice(unitPrice)
                .totalValue(total)
                .build();

        stockMovementRepository.save(movement);
    }


    public Page<StockMovementResponse> findAll(Pageable pageable) {
        return stockMovementRepository.findAll(pageable)
                .map(StockMovementResponse::response);
    }

    public Page<StockMovementResponse> findByPart(UUID partId, Pageable pageable) {
        return stockMovementRepository.findByPartId(partId, pageable)
                .map(StockMovementResponse::response);
    }

}
