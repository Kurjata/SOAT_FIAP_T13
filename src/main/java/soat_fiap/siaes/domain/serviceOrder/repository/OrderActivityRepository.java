package soat_fiap.siaes.domain.serviceOrder.repository;

import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;

import java.util.Optional;
import java.util.UUID;

public interface OrderActivityRepository {
    Optional<OrderActivity> findById(UUID id);
    OrderActivity save(OrderActivity item);
    void delete(OrderActivity item);
}
