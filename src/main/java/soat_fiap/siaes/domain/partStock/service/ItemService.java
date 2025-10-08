package soat_fiap.siaes.domain.partStock.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.partStock.enums.ItemType;
import soat_fiap.siaes.domain.partStock.enums.MovimentTypeEnum;
import soat_fiap.siaes.domain.partStock.model.Item;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.repository.ItemRepository;
import soat_fiap.siaes.interfaces.partStock.dto.UpdatePartRequest;

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
    public void updateInStock(UUID id, MovimentTypeEnum movimentTypeEnum, Integer quantity){

        Item item = this.findById(id);
        if(ItemType.PART.equals(item.getType())){
            switch (movimentTypeEnum){
                case ADD -> {((Part) item).add(quantity); ((Part) item).minusReserved(quantity);}
                case MINUS -> {((Part) item).minus(quantity); ((Part) item).addReserved(quantity);}
            }
            this.update(item);
        }
    }

    private void update(Item item) {
        this.itemRepository.save(item);
    }
}
