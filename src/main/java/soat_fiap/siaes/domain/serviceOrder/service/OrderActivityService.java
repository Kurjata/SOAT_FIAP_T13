package soat_fiap.siaes.domain.serviceOrder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrder.model.OrderActivity;
import soat_fiap.siaes.domain.serviceLabor.repository.ServiceLaborRepository;
import soat_fiap.siaes.domain.serviceOrder.repository.ServiceOrderRepository;
import soat_fiap.siaes.domain.serviceOrder.repository.OrderActivityRepository;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityRequest;
import soat_fiap.siaes.interfaces.serviceOrder.dto.OrderActivityResponse;
import soat_fiap.siaes.interfaces.serviceOrder.dto.ActivityItemRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderActivityService {
    private final OrderActivityRepository repository;
    private final ServiceOrderRepository orderRepository;
    private final ServiceLaborRepository laborRepository;
    private final ActivityItemService supplyService;

    // Listar itens de uma ordem
    public List<OrderActivityResponse> findByServiceOrder(UUID orderId) {
        ServiceOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada"));
        return order.getOrderActivities().stream()
                .map(OrderActivityResponse::new)
                .collect(Collectors.toList());
    }

    // Consultar item específico
    public OrderActivityResponse findById(UUID id) {
        OrderActivity item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));
        return new OrderActivityResponse(item);
    }

    // Criar item
    @Transactional
    public OrderActivityResponse create(OrderActivityRequest request) {
        validateRequest(request);

        ServiceOrder order = orderRepository.findById(request.serviceOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada"));

        ServiceLabor labor = laborRepository.findById(request.serviceLaborId())
                .orElseThrow(() -> new EntityNotFoundException("Serviço de mão de obra não encontrado"));

        OrderActivity item = new OrderActivity();
        item.setServiceOrder(order);
        item.setServiceLabor(labor);

        // Persistir item primeiro para poder associar insumos
        OrderActivity savedItem = repository.save(item);

        // Criar insumos, se houver
        if (request.items() != null) {
            for (ActivityItemRequest supplyRequest : request.items()) {
                // Associar o ID do item criado ao request
                ActivityItemRequest supplyWithItemId = new ActivityItemRequest(
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

        ServiceLabor labor = laborRepository.findById(request.serviceLaborId())
                .orElseThrow(() -> new EntityNotFoundException("Serviço de mão de obra não encontrado"));

        item.setServiceLabor(labor);

        OrderActivity updatedItem = repository.save(item);

        // Atualizar insumos (simplificado: deletar todos e recriar)
        if (request.items() != null) {
            if (updatedItem.getOrderItems() != null) {
                updatedItem.getOrderItems().forEach(s -> supplyService.delete(s.getId()));
            }
            for (ActivityItemRequest supplyRequest : request.items()) {
                ActivityItemRequest supplyWithItemId = new ActivityItemRequest(
                        updatedItem.getId(),
                        supplyRequest.itemId(),
                        supplyRequest.quantity()
                );
                supplyService.create(supplyWithItemId);
            }
        }

        return new OrderActivityResponse(updatedItem);
    }

    // Excluir item
    @Transactional
    public void delete(UUID id) {
        OrderActivity item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        // Excluir todos os insumos do item
        if (item.getOrderItems() != null) {
            item.getOrderItems().forEach(s -> supplyService.delete(s.getId()));
        }
        //se excluir todos os itens, faz sentido cancelar a ordem de serviço? Ou exclui-la?

        repository.delete(item);
    }

    // Validação do request
    private void validateRequest(OrderActivityRequest request) {
        if (request.serviceOrderId() == null) {
            throw new IllegalArgumentException("O ID da ordem de serviço é obrigatório");
        }
        if (request.serviceLaborId() == null) {
            throw new IllegalArgumentException("O ID do serviço de mão de obra é obrigatório");
        }
    }
}
