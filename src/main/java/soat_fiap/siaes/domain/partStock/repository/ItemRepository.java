package soat_fiap.siaes.domain.partStock.repository;

import soat_fiap.siaes.domain.partStock.model.Item;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {
    Optional<Item> findById(UUID id);
    Item save(Item item);
}
