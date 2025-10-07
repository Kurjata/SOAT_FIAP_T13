package soat_fiap.siaes.domain.partStock.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.partStock.model.Item;
import soat_fiap.siaes.domain.partStock.repository.ItemRepository;

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
}
