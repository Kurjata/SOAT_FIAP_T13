package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceLabor.service.ServiceLaborService;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.AddOrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderActivity.OrderActivityResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.orderItem.AddOrderItemRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderActivityService {
    private final OrderActivityRepository repository;
    private final ServiceOrderService serviceOrderService;
    private final OrderItemService supplyService;
    private final ServiceLaborService serviceLaborService;

    public List<OrderActivityResponse> findByServiceOrder(UUID orderId) {
        ServiceOrder order = serviceOrderService.findByUUID(orderId);
        return order.getOrderActivities().stream()
                .map(OrderActivityResponse::new)
                .collect(Collectors.toList());
    }

    public OrderActivityResponse findById(UUID id) {
        OrderActivity orderActivity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atividade de ordem não encontrado"));

        return new OrderActivityResponse(orderActivity);
    }

    @Transactional
    public OrderActivityResponse create(AddOrderActivityRequest request) {
        ServiceOrder order = serviceOrderService.findByUUID(request.serviceOrderId());
        ServiceLabor labor = serviceLaborService.findEntityById(request.serviceLaborId());

        OrderActivity orderActivity = new OrderActivity(order, labor);
        OrderActivity orderActivitySaved = repository.save(orderActivity);

        if (request.items() != null) {
            for (AddOrderItemRequest supplyRequest : request.items()) {
                AddOrderItemRequest supplyWithItemId = new AddOrderItemRequest(
                        orderActivitySaved.getId(),
                        supplyRequest.itemId(),
                        supplyRequest.quantity()
                );
                supplyService.create(supplyWithItemId);
            }
        }

        return new OrderActivityResponse(orderActivitySaved);
    }

    @Transactional
    public OrderActivityResponse update(UUID id, AddOrderActivityRequest request) {
        OrderActivity item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        ServiceLabor labor = serviceLaborService.findEntityById(request.serviceLaborId());
        item.setServiceLabor(labor);
        OrderActivity updatedItem = repository.save(item);

        if (request.items() != null) {
            if (updatedItem.getOrderItems() != null) {
                updatedItem.getOrderItems().forEach(s -> supplyService.delete(s.getId()));
            }
            for (AddOrderItemRequest supplyRequest : request.items()) {
                AddOrderItemRequest supplyWithItemId = new AddOrderItemRequest(
                        updatedItem.getId(),
                        supplyRequest.itemId(),
                        supplyRequest.quantity()
                );
                supplyService.create(supplyWithItemId);
            }
        }

        return new OrderActivityResponse(updatedItem);
    }

    @Transactional
    public void delete(UUID id) {
        OrderActivity item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        if (item.getOrderItems() != null) {
            item.getOrderItems().forEach(s -> supplyService.delete(s.getId()));
        }

        repository.delete(item);
    }

}
