package soat_fiap.siaes.domain.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import soat_fiap.siaes.domain.inventory.model.MovementType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.StockMovement;
import soat_fiap.siaes.domain.inventory.repository.StockMovementRepository;
import soat_fiap.siaes.interfaces.inventory.dto.StockMovementResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public void registerMovement(Part part, MovementType type, Integer quantity) {
        validateInputs(part, type, quantity);

        int before = part.getQuantity() != null ? part.getQuantity() : 0;
        int after = before + quantity;

        BigDecimal unitPrice = part.getUnitPrice() != null ? part.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(Math.abs(quantity)));

        StockMovement movement = new StockMovement(
                part,
                type,
                quantity,
                before,
                after,
                unitPrice,
                total
        );

        stockMovementRepository.save(movement);
    }

    private void validateInputs(Part part, MovementType type, Integer quantity) {
        Assert.notNull(part, "A peça não pode ser nula");
        Assert.notNull(type, "O tipo de movimentação não pode ser nulo");
        Assert.notNull(quantity, "A quantidade não pode ser nula");
        Assert.isTrue(quantity != 0, "A quantidade deve ser diferente de zero");
        Assert.notNull(part.getQuantity(), "A quantidade da peça não pode ser nula");
        Assert.notNull(part.getUnitPrice(), "O preço unitário da peça não pode ser nulo");
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
