package soat_fiap.siaes.infrastructure.persistence.partStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.partStock.model.Supply;
import soat_fiap.siaes.domain.partStock.repository.SupplyRepository;

import java.util.UUID;

@Repository
public interface SupplyJpaRepository  extends JpaRepository<Supply, UUID>, SupplyRepository {
}
