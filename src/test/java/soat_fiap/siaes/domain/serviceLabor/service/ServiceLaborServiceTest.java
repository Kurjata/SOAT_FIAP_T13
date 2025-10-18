package soat_fiap.siaes.domain.serviceLabor.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborRequest;
import soat_fiap.siaes.interfaces.serviceLabor.dto.ServiceLaborResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceLaborServiceTest {
    private ServiceLaborService service;
    private ServiceLaborRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(ServiceLaborRepository.class);
        service = new ServiceLaborService(repository);
    }

    private ServiceLabor createLabor(String description, BigDecimal cost) {
        return new ServiceLabor(description, cost);
    }

    @Test
    void save__should_return_saved_labor_when_data_is_correct() {
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", BigDecimal.valueOf(150.0));
        ServiceLabor labor = createLabor(request.description(), request.laborCost());
        ReflectionTestUtils.setField(labor, "id", UUID.randomUUID());

        when(repository.existsByDescription(request.description())).thenReturn(false);
        when(repository.save(any(ServiceLabor.class))).thenReturn(labor);

        ServiceLaborResponse response = service.save(request);

        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.laborCost()).isEqualTo(request.laborCost());
        assertNotNull(response.id());
        verify(repository).existsByDescription(request.description());
        verify(repository).save(any(ServiceLabor.class));
    }

    @Test
    void save__should_throw_exception_when_description_exists() {
        ServiceLaborRequest request = new ServiceLaborRequest("Troca de óleo", BigDecimal.valueOf(150.0));
        when(repository.existsByDescription(request.description())).thenReturn(true);

        assertThatThrownBy(() -> service.save(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Já existe um serviço com essa descrição.");

        verify(repository, never()).save(any());
    }

    @Test
    void findById__should_return_labor_when_exists() {
        UUID laborId = UUID.randomUUID();
        ServiceLabor labor = createLabor("Alinhamento", BigDecimal.valueOf(200.0));
        ReflectionTestUtils.setField(labor, "id", laborId);

        when(repository.findById(laborId)).thenReturn(Optional.of(labor));

        ServiceLaborResponse response = service.findById(laborId);

        assertThat(response).isNotNull();
        assertThat(response.description()).isEqualTo(labor.getDescription());
        assertThat(response.laborCost()).isEqualTo(labor.getLaborCost());
        verify(repository).findById(laborId);
    }

    @Test
    void findById__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Serviço de mão de obra com ID " + id + " não encontrado");
    }

    @Test
    void update__should_return_updated_labor_when_data_is_ok() {
        UUID id = UUID.randomUUID();
        ServiceLaborRequest request = new ServiceLaborRequest("Alinhamento", BigDecimal.valueOf(200.0));

        ServiceLabor labor = createLabor("Troca de óleo", BigDecimal.valueOf(150.0));
        ReflectionTestUtils.setField(labor, "id", id);

        when(repository.findById(id)).thenReturn(Optional.of(labor));
        when(repository.existsByDescriptionAndIdNot(request.description(), id)).thenReturn(false);
        when(repository.save(any(ServiceLabor.class))).thenReturn(labor);

        ServiceLaborResponse response = service.update(id, request);

        assertThat(response.description()).isEqualTo(request.description());
        assertThat(response.laborCost()).isEqualTo(request.laborCost());
        assertThat(response.id()).isEqualTo(id.toString());
        verify(repository).save(labor);
    }

    @Test
    void update__should_throw_exception_when_description_exists() {
        UUID id = UUID.randomUUID();
        ServiceLaborRequest request = new ServiceLaborRequest("Alinhamento", BigDecimal.valueOf(200.0));
        ServiceLabor labor = createLabor("Troca de óleo", BigDecimal.valueOf(150.0));

        when(repository.findById(id)).thenReturn(Optional.of(labor));
        when(repository.existsByDescriptionAndIdNot(request.description(), id)).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Já existe um serviço com essa descrição.");

        verify(repository, never()).save(any());
    }

    @Test
    void deleteById__should_delete_when_labor_exists() {
        ServiceLabor labor = createLabor("Troca de óleo", BigDecimal.valueOf(150.0));
        when(repository.findById(labor.getId())).thenReturn(Optional.of(labor));

        service.deleteById(labor.getId());

        verify(repository).delete(labor);
    }

    @Test
    void deleteById__should_throw_exception_when_labor_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Serviço de mão de obra com ID " + id + " não encontrado");

        verify(repository, never()).delete(any());
    }

    @Test
    void findAll__should_return_page_of_labor() {
        Pageable pageable = PageRequest.of(0, 10);

        ServiceLabor serviceLabor1 = createLabor("Troca de óleo", BigDecimal.valueOf(150.0));
        ServiceLabor serviceLabor2 = createLabor("Alinhamento", BigDecimal.valueOf(200.0));
        ReflectionTestUtils.setField(serviceLabor1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(serviceLabor2, "id", UUID.randomUUID());

        List<ServiceLabor> labors = List.of(serviceLabor1, serviceLabor2);

        Page<ServiceLabor> page = new PageImpl<>(labors, pageable, labors.size());
        Page<ServiceLaborResponse> pageExpected = page.map(ServiceLaborResponse::new);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<ServiceLaborResponse> result = service.findAll(pageable);

        assertThat(result).isEqualTo(pageExpected);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(repository).findAll(pageable);
    }
}