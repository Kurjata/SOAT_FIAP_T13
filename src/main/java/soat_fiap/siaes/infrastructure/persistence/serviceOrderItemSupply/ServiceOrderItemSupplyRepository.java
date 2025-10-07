package soat_fiap.siaes.infrastructure.persistence.serviceOrderItemSupply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ActivityItem;

import java.util.UUID;

@Repository
public interface ServiceOrderItemSupplyRepository extends JpaRepository<ActivityItem, UUID> {
}
