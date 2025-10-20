package soat_fiap.siaes.domain.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.inventory.model.StockMovement;

import java.util.UUID;

public interface StockMovementRepository {
    Page<StockMovement> findAll(Pageable pageable);
    Page<StockMovement> findByPartId(UUID partId, Pageable pageable);
    StockMovement save(StockMovement stockMovement);
}

