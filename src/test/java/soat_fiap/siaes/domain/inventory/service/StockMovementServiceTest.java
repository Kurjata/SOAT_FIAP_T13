package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.inventory.model.MovementType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.StockMovement;
import soat_fiap.siaes.domain.inventory.repository.StockMovementRepository;
import soat_fiap.siaes.interfaces.inventory.dto.StockMovementResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class StockMovementServiceTest {
    private StockMovementService service;
    private StockMovementRepository repository;
    private PartService partService;
    private Part part;

    @BeforeEach
    void setUp() {
        repository = mock(StockMovementRepository.class);
        partService = mock(PartService.class);
        service = new StockMovementService(repository, partService);
        part = mock(Part.class);
        when(part.getUnitPrice()).thenReturn(BigDecimal.TEN);
    }

    private StockMovement createStockMovement(MovementType movementType, int quantity, int balanceBefore, int balanceAfter) {
        return new StockMovement(part, movementType, quantity, balanceBefore, balanceAfter);
    }

    @Test
    void findAll__should_return_page_of_stock_movements() {
        Pageable pageable = PageRequest.of(0, 10);
        StockMovement m1 = createStockMovement(MovementType.ENTRADA, 5, 15, 20);
        StockMovement m2 = createStockMovement(MovementType.SAIDA_OS, 2, 20, 18);

        Page<StockMovement> page = new PageImpl<>(List.of(m1, m2), pageable, 2);
        when(repository.findAll(pageable)).thenReturn(page);

        Page<StockMovementResponse> result = service.findAll(pageable);

        assertThat(result).hasSize(2);
        assertThat(result.getContent()).isEqualTo(List.of(StockMovementResponse.response(m1), StockMovementResponse.response(m2)));
        verify(repository).findAll(pageable);
    }

    @Test
    void findByPart__should_return_page_of_movements_for_specific_part() {
        UUID partId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        StockMovement m1 = createStockMovement(MovementType.ENTRADA, 5, 15, 20);

        Page<StockMovement> page = new PageImpl<>(List.of(m1), pageable, 1);
        when(repository.findByPartId(partId, pageable)).thenReturn(page);

        Page<StockMovementResponse> result = service.findByPart(partId, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent()).isEqualTo(List.of(StockMovementResponse.response(m1)));
        verify(repository).findByPartId(partId, pageable);
    }

    @ParameterizedTest
    @EnumSource(MovementType.class)
    void registerMovement__should_save_new_stock_movement_when_data_is_valid(MovementType movementType) {
        UUID partId = UUID.randomUUID();

        when(partService.findById(partId)).thenReturn(part);

        service.registerMovement(partId, movementType, 5, 30, 25);

        verify(partService).findById(partId);
        verify(repository).save(any(StockMovement.class));
    }

    @Test
    void registerMovement__should_throw_exception_when_part_not_found() {
        UUID partId = UUID.randomUUID();
        when(partService.findById(partId)).thenThrow(new EntityNotFoundException("Peça não encontrada"));

        assertThatThrownBy(() -> service.registerMovement(partId, MovementType.ENTRADA, 5, 30, 25))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Peça não encontrada");

        verify(repository, never()).save(any());
    }
}