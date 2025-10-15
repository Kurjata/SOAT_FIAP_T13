package soat_fiap.siaes.infrastructure.persistence.serviceOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderItemRepository;

import java.util.UUID;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItem, UUID>, OrderItemRepository {
}
