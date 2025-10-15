package soat_fiap.siaes.domain.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import soat_fiap.siaes.domain.inventory.model.StockMovement;

import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findAll(Pageable pageable);

    Page<StockMovement> findByPartId(UUID partId, Pageable pageable);

}

