package soat_fiap.siaes.infrastructure.persistence.serviceOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;

import java.util.UUID;

@Repository
public interface OrderActivityJpaRepository extends JpaRepository<OrderActivity, UUID>, OrderActivityRepository {
}
