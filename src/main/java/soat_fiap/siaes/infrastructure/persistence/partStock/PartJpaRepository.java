package soat_fiap.siaes.infrastructure.persistence.partStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.repository.PartRepository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartJpaRepository extends JpaRepository<Part, UUID>, PartRepository {

    @Query("SELECT p FROM Part p WHERE p.quantity < p.minimumStockQuantity")
    List<Part> findPartsBelowMinimumStock();
}
