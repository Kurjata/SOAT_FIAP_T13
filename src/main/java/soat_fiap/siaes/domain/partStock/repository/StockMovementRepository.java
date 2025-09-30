package soat_fiap.siaes.domain.partStock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import soat_fiap.siaes.domain.partStock.model.StockMovement;

import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Page<StockMovement> findByPartId(UUID partId, Pageable pageable);

}
