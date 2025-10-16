package soat_fiap.siaes.infrastructure.persistence.serviceOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;

import java.util.UUID;

@Repository
public interface ServiceOrderJpaRepository extends JpaRepository<ServiceOrder, UUID>, ServiceOrderRepository {
    @Query(value = "SELECT * FROM service_orders o " +
            "JOIN users u ON o.user_id = u.user_id " +
            "WHERE u.document = :cpfCnpj",
            countQuery = "SELECT count(*) FROM service_orders o " +
                    "JOIN users u ON o.user_id = u.user_id " +
                    "WHERE u.document = :cpfCnpj",
            nativeQuery = true)
    Page<ServiceOrder> findByUserDocumentValue(@Param("cpfCnpj") String cpfCnpj, Pageable pageable);
    Page<ServiceOrder> findByUserId(UUID userId, Pageable pageable);
    Page<ServiceOrder> findByVehicleId(UUID vehicleId, Pageable pageable);
    Page<ServiceOrder> findByVehiclePlateIgnoreCase(String plate, Pageable pageable);
}
