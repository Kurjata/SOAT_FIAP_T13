package soat_fiap.siaes.infrastructure.persistence.serviceOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.domain.user.model.document.Document;

import java.util.UUID;

@Repository
public interface ServiceOrderJpaRepository extends JpaRepository<ServiceOrder, UUID>, ServiceOrderRepository {
    @Query("SELECT o FROM ServiceOrder o JOIN o.user u WHERE u.document = :cpfCnpj")
    Page<ServiceOrder> findByUserDocumentValue(@Param("cpfCnpj") Document cpfCnpj, Pageable pageable);
    Page<ServiceOrder> findByUserId(UUID userId, Pageable pageable);
    Page<ServiceOrder> findByVehicleId(UUID vehicleId, Pageable pageable);
    Page<ServiceOrder> findByVehiclePlateIgnoreCase(String plate, Pageable pageable);
}
