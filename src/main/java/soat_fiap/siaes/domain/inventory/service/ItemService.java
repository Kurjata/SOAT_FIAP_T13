package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.repository.ItemRepository;

import java.util.UUID;


@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item findById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));
    }

    @Transactional
    public void processStockMovement(UUID id, StockOperation stockOperation, Integer quantity) {
        Assert.notNull(quantity, "A quantidade não pode ser nula");
        Assert.isTrue(quantity > 0, "A quantidade deve ser maior que zero");

        Item item = findById(id);
        item.handleStockOperation(stockOperation, quantity);
        itemRepository.save(item);
    }
}
