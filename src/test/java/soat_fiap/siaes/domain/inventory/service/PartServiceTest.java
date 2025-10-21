package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;
import soat_fiap.siaes.domain.inventory.repository.PartRepository;
import soat_fiap.siaes.interfaces.inventory.dto.UpdatePartRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PartServiceTest {

    private PartService service;
    private PartRepository repository;

    @BeforeEach
    void setUp() {
        repository = mock(PartRepository.class);
        service = new PartService(repository);
    }

    private Part createPart(BigDecimal unitPrice, UnitMeasure unitMeasure, int quantity, int minimumStockQuantity) {
        return new Part("Parafuso", unitPrice, unitMeasure, quantity, 0, "1234567890123", "ABC Indústria", minimumStockQuantity);
    }

    @Test
    void findAll__should_return_page() {
        Pageable pageable = mock(Pageable.class);
        when(repository.findAll(pageable)).thenReturn(Page.empty());

        Page<Part> result = service.findAll(pageable);

        assertThat(result).isEmpty();
        verify(repository).findAll(pageable);
    }

    @Test
    void save__should_return_saved_part_when_data_is_ok() {
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.UNIT, 100, 20);

        when(repository.existsByEan(part.getEan())).thenReturn(false);
        when(repository.save(part)).thenReturn(part);

        Part saved = service.save(part);

        assertThat(saved).isNotNull();
        assertThat(saved.getEan()).isEqualTo(part.getEan());
        assertThat(saved.getQuantity()).isEqualTo(part.getQuantity());
        assertThat(saved.getManufacturer()).isEqualTo(part.getManufacturer());
        assertThat(saved.getUnitMeasure()).isEqualTo(part.getUnitMeasure());
        assertThat(saved.getReservedQuantity()).isEqualTo(part.getReservedQuantity());
        assertThat(saved.getMinimumStockQuantity()).isEqualTo(part.getMinimumStockQuantity());
        verify(repository).existsByEan(part.getEan());
        verify(repository).save(part);
    }

    @Test
    void save__should_throw_exception_when_ean_exists() {
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.UNIT, 100, 20);

        when(repository.existsByEan(part.getEan())).thenReturn(true);

        assertThatThrownBy(() -> service.save(part))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("EAN já existe: " + part.getEan());

        verify(repository, never()).save(any());
    }

    @Test
    void findById__should_return_part_when_exists() {
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.BOX, 15, 20);
        when(repository.findById(part.getId())).thenReturn(Optional.of(part));

        Part found = service.findById(part.getId());

        assertThat(found).isEqualTo(part);
        verify(repository).findById(part.getId());
    }

    @Test
    void findById__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Peça não encontrada com ID: " + id);
    }

    @Test
    void update__should_return_updated_part_when_data_is_ok() {
        UUID id = UUID.randomUUID();
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.UNIT, 100, 20);
        UpdatePartRequest request = new UpdatePartRequest("Parafuso Atualizado", BigDecimal.valueOf(0.60), UnitMeasure.UNIT, 150, 10, 25, "1234567890124", "XYZ Indústria");

        when(repository.findById(id)).thenReturn(Optional.of(part));
        when(repository.save(part)).thenReturn(part);

        Part updated = service.update(id, request);

        assertThat(updated.getName()).isEqualTo("Parafuso Atualizado");
        assertThat(updated.getUnitPrice()).isEqualTo(BigDecimal.valueOf(0.60));
        assertThat(updated.getQuantity()).isEqualTo(150);
        assertThat(updated.getReservedQuantity()).isEqualTo(10);
        assertThat(updated.getMinimumStockQuantity()).isEqualTo(25);
        assertThat(updated.getEan()).isEqualTo("1234567890124");
        assertThat(updated.getManufacturer()).isEqualTo("XYZ Indústria");
        verify(repository).save(part);
    }

    @Test
    void update__should_throw_exception_when_part_not_found() {
        UUID id = UUID.randomUUID();
        UpdatePartRequest request = mock(UpdatePartRequest.class);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Peça não encontrada com ID: " + id);

        verify(repository, never()).save(any());
    }

    @Test
    void update__should_throw_exception_when_ean_exists() {
        UUID id = UUID.randomUUID();
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.UNIT, 100, 20);
        UpdatePartRequest request = new UpdatePartRequest("Parafuso Atualizado", BigDecimal.valueOf(0.60), UnitMeasure.UNIT, 150, 10, 25, "1234567890124", "XYZ Indústria");

        when(repository.findById(id)).thenReturn(Optional.of(part));
        when(repository.existsByEanAndIdNot(request.ean(), id)).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("EAN já existe: " + request.ean());

        verify(repository, never()).save(any());
    }

    @Test
    void deleteById__should_delete_when_part_exists() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.deleteById(id);

        verify(repository).deleteById(id);
    }

    @Test
    void deleteById__should_throw_exception_when_part_not_found() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Peça não encontrada com ID: " + id);

        verify(repository, never()).deleteById(any());
    }

    @Test
    void findPartsBelowMinimumStock__should_return_list() {
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.UNIT, 4, 5);

        List<Part> parts = List.of(part);
        when(repository.findPartsBelowMinimumStock()).thenReturn(parts);

        List<Part> result = service.findPartsBelowMinimumStock();

        assertThat(result).containsExactly(part);
        verify(repository).findPartsBelowMinimumStock();
    }

    @Test
    void addStock_and_register_movement() {
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.UNIT, 4, 5);
        UUID id = part.getId();
        Integer quantityToAdd = 5;

        when(repository.findById(id)).thenReturn(Optional.of(part));
        when(repository.save(part)).thenReturn(part);

        Part updated = service.addStock(id, quantityToAdd);

        assertThat(updated.getQuantity()).isEqualTo(9);
        verify(repository).save(part);
    }

    @Test
    void updateStockQuantity__should_adjust_stock_and_register_movement() {
        Part part = createPart(BigDecimal.valueOf(0.50), UnitMeasure.UNIT, 4, 5);
        UUID id = part.getId();
        Integer quantityToAdjust = -3;

        when(repository.findById(id)).thenReturn(Optional.of(part));
        when(repository.save(part)).thenReturn(part);

        Part updated = service.updateStockQuantity(id, quantityToAdjust);

        assertThat(updated.getQuantity()).isEqualTo(1);
        verify(repository).save(part);
    }
}