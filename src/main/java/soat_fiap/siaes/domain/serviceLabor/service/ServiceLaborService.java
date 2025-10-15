package soat_fiap.siaes.domain.serviceLabor.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborRequest;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ServiceLaborService {
    private final ServiceLaborRepository repository;

    public Page<ServiceLaborResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(ServiceLaborResponse::new);
    }

    public ServiceLaborResponse findById(UUID id) {
        return new ServiceLaborResponse(this.findByUUID(id));
    }

    @Transactional
    public ServiceLaborResponse save(ServiceLaborRequest request) {
        // Validação de duplicidade
        if (repository.existsByDescription(request.description())) {
            throw new IllegalArgumentException("Já existe um serviço com essa descrição.");
        }

        ServiceLabor labor = new ServiceLabor();
        labor.setDescription(request.description());
        labor.setLaborCost(request.laborCost());

        return new ServiceLaborResponse(persist(labor));
    }

    @Transactional
    protected ServiceLabor persist(ServiceLabor labor) {
        try {
            return repository.save(labor);
        }
        catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Erro ao salvar a mão de obra: dados inválidos ou violação de restrição no banco de dados.");
        }
        catch (JpaSystemException | PersistenceException e) {
            throw new RuntimeException("Erro interno ao tentar persistir a mão de obra. Verifique a conexão com o banco ou a transação.");
        }
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.delete(this.findByUUID(id));
    }

    public ServiceLabor findByUUID(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço de mão de obra com ID " + id + " não encontrado"));
    }

    public ServiceLaborResponse update(UUID id, ServiceLaborRequest request) {
        ServiceLabor labor = this.findByUUID(id);

        if (repository.existsByDescriptionAndIdNot(request.description(), id)) {
            throw new IllegalArgumentException("Já existe um serviço com essa descrição.");
        }
        labor.setDescription(request.description());
        labor.setLaborCost(request.laborCost());

        return new ServiceLaborResponse(persist(labor));
    }
}
