package soat_fiap.siaes.infrastructure.persistence.ServiceLabor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;

import java.util.UUID;

@Repository
public interface ServiceLaborRepository extends JpaRepository<ServiceLabor, UUID> {
    // Verifica se já existe um registro com a descrição informada
    boolean existsByDescription(String description);

    // Verifica se já existe um registro com a descrição informada e ID diferente
    boolean existsByDescriptionAndIdNot(String description, UUID id);
}
