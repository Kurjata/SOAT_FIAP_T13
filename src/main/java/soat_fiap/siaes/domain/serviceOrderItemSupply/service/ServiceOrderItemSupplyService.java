package soat_fiap.siaes.domain.serviceOrderItemSupply.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.partStock.enums.ItemType;
import soat_fiap.siaes.domain.partStock.model.Item;
import soat_fiap.siaes.domain.partStock.repository.ItemRepository;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ActivityItem;
import soat_fiap.siaes.infrastructure.persistence.serviceOrderItem.ServiceOrderItemRepository;
import soat_fiap.siaes.infrastructure.persistence.serviceOrderItemSupply.ServiceOrderItemSupplyRepository;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ActivityItemRequest;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceOrderItemSupplyService {
    private final ServiceOrderItemSupplyRepository repository;
    private final ServiceOrderItemRepository serviceOrderItemRepository;
    private final ItemRepository itemRepository;

    // Listar insumos de um item
    public List<ServiceOrderItemSupplyResponse> findByServiceOrderItem(UUID itemId) {
        OrderActivity item = serviceOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));
        return item.getActivityItems().stream()
                .map(ServiceOrderItemSupplyResponse::new)
                .collect(Collectors.toList());
    }

    // Consultar insumo específico
    public ServiceOrderItemSupplyResponse findById(UUID id) {
        ActivityItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));
        return new ServiceOrderItemSupplyResponse(supply);
    }

    // Criar insumo
    @Transactional
    public ServiceOrderItemSupplyResponse create(ActivityItemRequest request) {
        validateRequest(request);

        Item partStock = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        OrderActivity item = serviceOrderItemRepository.findById(request.serviceOrderItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        ActivityItem supply = new ActivityItem();
        supply.setOrderActivity(item);
        supply.setPartStock(partStock);
        supply.setQuantity(request.quantity());
        if (ItemType.PART.equals(partStock.getType())) {
            supply.setUnitPrice(partStock.getUnitPrice());
        }
        ActivityItem saved = repository.save(supply);
        return new ServiceOrderItemSupplyResponse(saved);
    }

    // Atualizar insumo
    @Transactional
    public ServiceOrderItemSupplyResponse update(UUID id, ActivityItemRequest request) {
        validateRequest(request);

        ActivityItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        Item partStock = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        supply.setPartStock(partStock);
        supply.setQuantity(request.quantity());

        if (ItemType.PART.equals(partStock.getType())) {
            supply.setUnitPrice(partStock.getUnitPrice());
        }

        ActivityItem updated = repository.save(supply);
        return new ServiceOrderItemSupplyResponse(updated);
    }

    // Excluir insumo
    @Transactional
    public void delete(UUID id) {
        ActivityItem supply = repository.findById(id)
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
