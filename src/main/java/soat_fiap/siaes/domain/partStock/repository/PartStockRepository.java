package soat_fiap.siaes.domain.partStock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import soat_fiap.siaes.domain.partStock.model.PartStock;

import java.util.Optional;
import java.util.UUID;



public interface PartStockRepository extends JpaRepository<PartStock, UUID> {

    Optional<PartStock> findByEan(String ean);
    boolean existsByEan(String ean);

}
