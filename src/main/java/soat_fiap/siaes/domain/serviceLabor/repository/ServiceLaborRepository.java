package soat_fiap.siaes.domain.serviceLabor.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceLaborRepository {
    boolean existsByDescription(String description);
    boolean existsByDescriptionAndIdNot(String description, UUID id);
    Optional<ServiceLabor> findByDescription(String description);
    Optional<ServiceLabor> findById(UUID uuid);
    Page<ServiceLabor> findAll(Pageable pageable);
    ServiceLabor save(ServiceLabor labor);
    void delete(ServiceLabor serviceLabor);
}
