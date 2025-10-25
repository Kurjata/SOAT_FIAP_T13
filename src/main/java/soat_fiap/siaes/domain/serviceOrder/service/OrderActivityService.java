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
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrdemItemRequest;

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

    // Criar item
    @Transactional
    public OrderActivityResponse create(OrderActivityRequest request) {
        validateRequest(request);

        ServiceOrder order = serviceOrderService.findByUUID(request.serviceOrderId());
        ServiceLabor labor = serviceLaborService.findEntityById(request.serviceLaborId());

        OrderActivity item = new OrderActivity(order, labor);

        // Persistir item primeiro para poder associar insumos
        OrderActivity savedItem = repository.save(item);

        // Criar insumos, se houver
        if (request.items() != null) {
            for (OrdemItemRequest supplyRequest : request.items()) {
                // Associar o ID do item criado ao request
                OrdemItemRequest supplyWithItemId = new OrdemItemRequest(
                        savedItem.getId(),
                        supplyRequest.itemId(),
                        supplyRequest.quantity()
                );
                supplyService.create(supplyWithItemId);
            }
        }

        return new OrderActivityResponse(savedItem);
    }

    // Alterar item (apenas serviço ou insumos)
    @Transactional
    public OrderActivityResponse update(UUID id, OrderActivityRequest request) {
        validateRequest(request);

        OrderActivity item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        ServiceLabor labor = serviceLaborService.findEntityById(request.serviceLaborId());

        item.setServiceLabor(labor);

        OrderActivity updatedItem = repository.save(item);

        // Atualizar insumos (simplificado: deletar todos e recriar)
        if (request.items() != null) {
            if (updatedItem.getOrderItems() != null) {
                updatedItem.getOrderItems().forEach(s -> supplyService.delete(s.getId()));
            }
            for (OrdemItemRequest supplyRequest : request.items()) {
                OrdemItemRequest supplyWithItemId = new OrdemItemRequest(
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
        //se excluir todos os itens, faz sentido cancelar a ordem de serviço? Ou exclui-la?

        repository.delete(item);
    }

    private void validateRequest(OrderActivityRequest request) {
        if (request.serviceOrderId() == null) {
            throw new IllegalArgumentException("O ID da ordem de serviço é obrigatório");
        }
        if (request.serviceLaborId() == null) {
            throw new IllegalArgumentException("O ID do serviço de mão de obra é obrigatório");
        }
    }
}
