package soat_fiap.siaes.domain.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.inventory.model.MovementType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.StockMovement;
import soat_fiap.siaes.domain.inventory.repository.StockMovementRepository;
import soat_fiap.siaes.interfaces.inventory.dto.StockMovementResponse;

import java.util.UUID;

@Service
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final PartService partService;

    public StockMovementService(StockMovementRepository stockMovementRepository, PartService partService) {
        this.stockMovementRepository = stockMovementRepository;
        this.partService = partService;
    }

    public Page<StockMovementResponse> findAll(Pageable pageable) {
        return stockMovementRepository.findAll(pageable).map(StockMovementResponse::response);
    }

    public Page<StockMovementResponse> findByPart(UUID partId, Pageable pageable) {
        return stockMovementRepository.findByPartId(partId, pageable).map(StockMovementResponse::response);
    }

    @Transactional
    public void registerMovement(UUID id, MovementType type, int quantity, int balanceBefore, int balanceAfter) {
        Part part = partService.findById(id);
        StockMovement movement = new StockMovement(part, type, quantity, balanceBefore, balanceAfter);
        stockMovementRepository.save(movement);
    }
}
