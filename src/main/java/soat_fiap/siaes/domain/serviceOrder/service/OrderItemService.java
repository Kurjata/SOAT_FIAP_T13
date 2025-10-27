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
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.CreateOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.OrderItemResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository repository;
    private final OrderActivityRepository orderActivityRepository;
    private final ItemRepository itemRepository;

    public List<OrderItemResponse> findAllByOrderActivity(UUID orderActivityId) {
        OrderActivity item = orderActivityRepository.findById(orderActivityId)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));
        return item.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
    }

    public OrderItemResponse findById(UUID id) {
        OrderItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));
        return new OrderItemResponse(supply);
    }

    @Transactional
    public OrderItemResponse create(AddOrderItemRequest request) {
        Item partStock = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        OrderActivity orderActivity = orderActivityRepository.findById(request.orderActivityId())
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        OrderItem supply = new OrderItem(orderActivity, partStock, request.quantity(), partStock.getUnitPrice());
        if (ItemType.PART.equals(partStock.getType())) {
            supply.setUnitPrice(partStock.getUnitPrice());
        }
        OrderItem saved = repository.save(supply);
        return new OrderItemResponse(saved);
    }

    @Transactional
    public OrderItemResponse update(UUID id, CreateOrderItemRequest request) {
        OrderItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        Item partStock = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        supply.setPartStock(partStock);
        supply.setQuantity(request.quantity());

        if (ItemType.PART.equals(partStock.getType())) {
            supply.setUnitPrice(partStock.getUnitPrice());
        }

        OrderItem updated = repository.save(supply);
        return new OrderItemResponse(updated);
    }

    @Transactional
    public void delete(UUID id) {
        OrderItem supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));
        repository.delete(supply);
    }
}
