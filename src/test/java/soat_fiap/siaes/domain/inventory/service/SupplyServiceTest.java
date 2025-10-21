package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.repository.SupplyRepository;
import soat_fiap.siaes.interfaces.inventory.dto.UpdateSupplyRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SupplyServiceTest {
    private SupplyService service;
    private SupplyRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(SupplyRepository.class);
        service = new SupplyService(repository);
    }

    private Supply createSupply() {
        return new Supply("Filtro", BigDecimal.valueOf(25.5), UnitMeasure.UNIT, "Fornecedor X", true);
    }

    @Test
    void save__should_return_saved_supply_when_data_is_ok() {
        Supply supply = createSupply();

        when(repository.save(supply)).thenReturn(supply);

        Supply saved = service.save(supply);

        assertThat(saved).isEqualTo(supply);
        verify(repository).save(supply);
    }

    @Test
    void findAll__should_return_page() {
        Pageable pageable = PageRequest.of(0, 10);

        Supply supply1 = new Supply("Filtro", BigDecimal.valueOf(25.5), UnitMeasure.LT, "Fornecedor A", true);
        Supply supply2 = new Supply("Lubrificante", BigDecimal.valueOf(50.0), UnitMeasure.LT, "Fornecedor B", false);
        ReflectionTestUtils.setField(supply1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(supply2, "id", UUID.randomUUID());

        List<Supply> supplies = List.of(supply1, supply2);
        Page<Supply> pageExpected = new PageImpl<>(supplies, pageable, supplies.size());

        when(repository.findAll(pageable)).thenReturn(pageExpected);

        Page<Supply> result = service.findAll(pageable);

        assertThat(result).isEqualTo(pageExpected);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(repository).findAll(pageable);
    }

    @Test
    void findById__should_return_supply_when_exists() {
        Supply supply = createSupply();
        when(repository.findById(supply.getId())).thenReturn(Optional.of(supply));

        Supply found = service.findById(supply.getId());

        assertThat(found).isEqualTo(supply);
        verify(repository).findById(supply.getId());
    }

    @Test
    void findById__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado com ID: " + id);
    }

    @Test
    void update__should_update_existing_supply() {
        UUID id = UUID.randomUUID();
        Supply existing = createSupply();
        UpdateSupplyRequest request = new UpdateSupplyRequest("Novo Filtro", BigDecimal.valueOf(30), UnitMeasure.LT, "Fornecedor Y", false);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        Supply updated = service.update(id, request);

        assertThat(updated.getName()).isEqualTo("Novo Filtro");
        assertThat(updated.getUnitPrice()).isEqualTo(BigDecimal.valueOf(30));
        assertThat(updated.getUnitMeasure()).isEqualTo(UnitMeasure.LT);
        assertThat(updated.getSupplier()).isEqualTo("Fornecedor Y");
        assertThat(updated.getAvailable()).isFalse();

        verify(repository).save(existing);
    }

    @Test
    void update__should_throw_exception_when_supply_not_found() {
        UUID id = UUID.randomUUID();
        UpdateSupplyRequest request = mock(UpdateSupplyRequest.class);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado com ID: " + id);

        verify(repository, never()).save(any());
    }

    @Test
    void deleteById__should_delete_when_supply_exists() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteById(id);

        verify(repository).deleteById(id);
    }

    @Test
    void deleteById__should_throw_exception_when_supply_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado com ID: " + id);

        verify(repository, never()).deleteById(any());
    }

    @Test
    void updateAvailability__should_update_availability_when_found() {
        UUID id = UUID.randomUUID();
        Supply supply = createSupply();
        when(repository.findById(id)).thenReturn(Optional.of(supply));
        when(repository.save(supply)).thenReturn(supply);

        Supply updated = service.updateAvailability(id, false);

        assertThat(updated.getAvailable()).isFalse();
        verify(repository).save(supply);
    }

    @Test
    void updateAvailability__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateAvailability(id, true))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Insumo não encontrado com ID: " + id);

        verify(repository, never()).save(any());
    }
}