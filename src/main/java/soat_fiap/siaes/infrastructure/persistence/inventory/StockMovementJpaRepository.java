package soat_fiap.siaes.infrastructure.persistence.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.inventory.model.StockMovement;
import soat_fiap.siaes.domain.inventory.repository.StockMovementRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockMovementJpaRepository extends JpaRepository<StockMovement, UUID>, StockMovementRepository {
    Page<StockMovement> findByPartId(UUID partId, Pageable pageable);
}
