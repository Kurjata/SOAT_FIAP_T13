package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.inventory.enums.ItemType;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.repository.ItemRepository;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.model.OrderItem;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderItemRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ActivityItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ActivityItemResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityItemService {
    private final OrderItemRepository repository;
    private final OrderActivityRepository orderActivityRepository;
    private final ItemRepository itemRepository;

    // Listar insumos de um item
    public List<ActivityItemResponse> findByServiceOrderItem(UUID itemId) {
        OrderActivity item = orderActivityRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));
        return item.getOrderItems().stream()
                .map(ActivityItemResponse::new)
                .collect(Collectors.toList());
    }

    // Consultar insumo específico
    public ActivityItemResponse findById(UUID id) {
        OrderItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));
        return new ActivityItemResponse(supply);
    }

    // Criar insumo
    @Transactional
    public ActivityItemResponse create(ActivityItemRequest request) {
        validateRequest(request);

        Item partStock = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        OrderActivity item = orderActivityRepository.findById(request.serviceOrderItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        OrderItem supply = new OrderItem();
        supply.setOrderActivity(item);
        supply.setPartStock(partStock);
        supply.setQuantity(request.quantity());
        if (ItemType.PART.equals(partStock.getType())) {
            supply.setUnitPrice(partStock.getUnitPrice());
        }
        OrderItem saved = repository.save(supply);
        return new ActivityItemResponse(saved);
    }

    // Atualizar insumo
    @Transactional
    public ActivityItemResponse update(UUID id, ActivityItemRequest request) {
        validateRequest(request);

        OrderItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        Item partStock = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        supply.setPartStock(partStock);
        supply.setQuantity(request.quantity());

        if (ItemType.PART.equals(partStock.getType())) {
            supply.setUnitPrice(partStock.getUnitPrice());
        }

        OrderItem updated = repository.save(supply);
        return new ActivityItemResponse(updated);
    }

    // Excluir insumo
    @Transactional
    public void delete(UUID id) {
        OrderItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));
        repository.delete(supply);
    }

    // Validação de request
    private void validateRequest(ActivityItemRequest request) {
        if (request.itemId() == null) {
            throw new IllegalArgumentException("O ID do insumo é obrigatório");
        }
        if (request.quantity() == null || request.quantity() < 1) {
            throw new IllegalArgumentException("A quantidade do insumo deve ser no mínimo 1");
        }
        if (request.serviceOrderItemId() == null) {
            throw new IllegalArgumentException("O ID do item da ordem é obrigatório");
        }
    }
}
