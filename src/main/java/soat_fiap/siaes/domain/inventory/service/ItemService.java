package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.repository.ItemRepository;
import soat_fiap.siaes.shared.BusinessException;

import java.util.UUID;

import static soat_fiap.siaes.domain.inventory.model.MovementType.*;

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
    public void updateInStock(UUID id, StockOperation stockOperation, Integer quantity) {
        Assert.notNull(quantity, "A quantidade não pode ser nula");
        Assert.isTrue(quantity > 0, "A quantidade deve ser maior que zero");

        Item item = findById(id);

        if (item instanceof Supply supply) {
            if (!supply.getAvailable())
                throw new BusinessException("Não há insumo disponível em estoque para o item: " + supply.getName());
            return;
        }

        if (item instanceof Part part) {
            switch (stockOperation) {
                case RESERVE_STOCK -> handleReserve(part, quantity);
                case CONFIRM_RESERVATION -> handleConfirmReservation(part, quantity);
                case CANCEL_RESERVATION -> handleCancelReservation(part, quantity);
            }
        }

        itemRepository.save(item);
    }

    private void handleReserve(Part part, int quantity) {
        stockMovementService.registerMovement(part, SAIDA_OS, quantity);
        part.moveToReserved(quantity);
    }

    private void handleConfirmReservation(Part part, int quantity) {
        part.consumeReserved(quantity);
    }

    private void handleCancelReservation(Part part, int quantity) {
        stockMovementService.registerMovement(part, DEVOLUCAO_OS, quantity);
        part.moveToAvailable(quantity);
    }
}
