package soat_fiap.siaes.domain.inventory.repository;

import soat_fiap.siaes.domain.inventory.model.Item;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {
    Optional<Item> findById(UUID id);
    Item save(Item item);
}
