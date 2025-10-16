package soat_fiap.siaes.infrastructure.persistence.serviceLabor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceLaborJpaRepository extends JpaRepository<ServiceLabor, UUID>, ServiceLaborRepository {
    boolean existsByDescription(String description);
    boolean existsByDescriptionAndIdNot(String description, UUID id);
    Optional<ServiceLabor> findByDescription(String description);
}
