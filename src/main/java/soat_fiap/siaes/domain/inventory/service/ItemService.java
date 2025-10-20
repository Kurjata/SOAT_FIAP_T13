package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.inventory.enums.ItemType;
import soat_fiap.siaes.domain.inventory.enums.MovimentType;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.model.MovementType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.repository.ItemRepository;
import soat_fiap.siaes.shared.BusinessException;

import java.util.UUID;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final StockMovementService stockMovementService;

    public ItemService(ItemRepository itemRepository, StockMovementService stockMovementService) {
        this.itemRepository = itemRepository;
        this.stockMovementService = stockMovementService;
    }


    public Item findById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));
    }

    @Transactional
    public void updateInStock(UUID id, MovimentType movimentType, Integer quantity, Boolean isRemoveReserved) {
        if (quantity == null || quantity <= 0) return; // ignora quantidades inválidas

        Item item = this.findById(id);
        if (ItemType.PART.equals(item.getType())) {
            Part part = (Part) item;

            switch (movimentType) {
                // Tira do estoque principal e remove reserva se necessário
                case MINUS -> {
                    if (Boolean.TRUE.equals(isRemoveReserved)) {
                        // Remove da reserva
                        part.minusReserved(quantity);
                    } else {
                        // Verifica estoque principal
                        int available = part.getQuantity() != null ? part.getQuantity() : 0;
                        if (available < quantity) {
                            throw new BusinessException("Não há quantidade suficiente em estoque para o item: " + part.getName());
                        }
                        part.minus(quantity);         // Tira do estoque
                        part.addReserved(quantity);   // Coloca para reserva
                        stockMovementService.registerMovement(part, MovementType.SAIDA_OS, quantity);
                    }
                }
                // Devolve para estoque e remove da reserva
                case ADD -> {
                    part.add(quantity);           // Adiciona ao estoque
                    part.minusReserved(quantity); // Remove da reserva, se houver
                    stockMovementService.registerMovement(part, MovementType.DEVOLUCAO_OS, quantity);
                }
            }

            this.update(part);
        }
    }

    private void update(Item item) {
        this.itemRepository.save(item);
    }
}
