package soat_fiap.siaes.infrastructure.persistence.partStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.repository.PartRepository;

import java.util.UUID;

@Repository
public interface PartJpaRepository extends JpaRepository<Part, UUID>, PartRepository {


}
