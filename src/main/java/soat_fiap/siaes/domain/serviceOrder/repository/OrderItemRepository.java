package soat_fiap.siaes.domain.serviceOrder.repository;

import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;

import java.util.Optional;
import java.util.UUID;

public interface OrderItemRepository {
    Optional<OrderItem> findById(UUID id);
    OrderItem save(OrderItem orderItem);
    void delete(OrderItem orderItem);
}
