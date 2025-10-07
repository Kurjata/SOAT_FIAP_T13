package soat_fiap.siaes.infrastructure.persistence.serviceOrderItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;

import java.util.UUID;

@Repository
public interface ServiceOrderItemRepository extends JpaRepository<OrderActivity, UUID> {
}
