package soat_fiap.siaes.infrastructure.persistence.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.repository.PartRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartJpaRepository extends JpaRepository<Part, UUID>, PartRepository {

    @Query("SELECT p FROM Part p WHERE p.quantity < p.minimumStockQuantity")
    List<Part> findPartsBelowMinimumStock();
    boolean existsByEanAndIdNot(String ean, UUID id);
}
