package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.repository.ItemRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ItemServiceTest {
    private ItemRepository itemRepository;
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        itemService = new ItemService(itemRepository);
    }

    private Item createItem() {
        return mock(Item.class);
    }

    @Test
    void findById__should_return_item_when_exists() {
        UUID id = UUID.randomUUID();
        Item item = createItem();
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        Item result = itemService.findById(id);

        assertThat(result).isEqualTo(item);
        verify(itemRepository).findById(id);
    }

    @Test
    void findById__should_throw_exception_when_not_found() {
        UUID id = UUID.randomUUID();
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item não encontrado");

        verify(itemRepository).findById(id);
    }

    @Test
    void processStockMovement__should_call_handleStockOperation_and_save() {
        UUID id = UUID.randomUUID();
        Item item = createItem();
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        itemService.processStockMovement(id, StockOperation.RESERVE_STOCK, 10);

        verify(item).handleStockOperation(StockOperation.RESERVE_STOCK, 10);
        verify(itemRepository).save(item);
    }

    @Test
    void processStockMovement__should_throw_exception_when_quantity_is_null() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> itemService.processStockMovement(id, StockOperation.RESERVE_STOCK, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A quantidade não pode ser nula");

        verifyNoInteractions(itemRepository);
    }

    @Test
    void processStockMovement__should_throw_exception_when_quantity_is_zero_or_negative() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> itemService.processStockMovement(id, StockOperation.RESERVE_STOCK, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A quantidade deve ser maior que zero");

        assertThatThrownBy(() -> itemService.processStockMovement(id, StockOperation.RESERVE_STOCK, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A quantidade deve ser maior que zero");

        verifyNoInteractions(itemRepository);
    }

    @Test
    void processStockMovement__should_throw_exception_when_item_not_found() {
        UUID id = UUID.randomUUID();
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.processStockMovement(id, StockOperation.RESERVE_STOCK, 5))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Item não encontrado");

        verify(itemRepository).findById(id);
        verify(itemRepository, never()).save(any());
    }
}