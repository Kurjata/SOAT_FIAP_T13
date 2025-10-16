package soat_fiap.siaes.domain.serviceOrder.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;


import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;


public interface ServiceOrderRepository {
    Optional<ServiceOrder> findById(UUID id);
    Page<ServiceOrder> findByUserDocumentValue(@Param("cpfCnpj") String cpfCnpj, Pageable pageable);
    Page<ServiceOrder> findByUserId(UUID userId, Pageable pageable);
    Page<ServiceOrder> findByVehicleId(UUID vehicleId, Pageable pageable);
    Page<ServiceOrder> findByVehiclePlateIgnoreCase(String plate, Pageable pageable);
    Page<ServiceOrder> findAll(Pageable pageable);
    ServiceOrder save(ServiceOrder serviceOrder);
    void delete(ServiceOrder order);
}
