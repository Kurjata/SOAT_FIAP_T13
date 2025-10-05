package soat_fiap.siaes.domain.serviceOrderItem.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrderItem.model.ServiceOrderItem;
import soat_fiap.siaes.domain.serviceOrderItemSupply.service.ServiceOrderItemSupplyService;
import soat_fiap.siaes.infrastructure.persistence.ServiceLabor.ServiceLaborRepository;
import soat_fiap.siaes.infrastructure.persistence.serviceOrder.ServiceOrderRepository;
import soat_fiap.siaes.infrastructure.persistence.serviceOrderItem.ServiceOrderItemRepository;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.ServiceOrderItemRequest;
import soat_fiap.siaes.interfaces.serviceOrderItem.dto.ServiceOrderItemResponse;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceOrderItemService {
    private final ServiceOrderItemRepository repository;
    private final ServiceOrderRepository orderRepository;
    private final ServiceLaborRepository laborRepository;
    private final ServiceOrderItemSupplyService supplyService;

    // Listar itens de uma ordem
    public List<ServiceOrderItemResponse> findByServiceOrder(UUID orderId) {
        ServiceOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada"));
        return order.getItems().stream()
                .map(ServiceOrderItemResponse::new)
                .collect(Collectors.toList());
    }

    // Consultar item específico
    public ServiceOrderItemResponse findById(UUID id) {
        ServiceOrderItem item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));
        return new ServiceOrderItemResponse(item);
    }

    // Criar item
    @Transactional
    public ServiceOrderItemResponse create(ServiceOrderItemRequest request) {
        validateRequest(request);

        ServiceOrder order = orderRepository.findById(request.serviceOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Ordem de serviço não encontrada"));

        ServiceLabor labor = laborRepository.findById(request.serviceLaborId())
                .orElseThrow(() -> new EntityNotFoundException("Serviço de mão de obra não encontrado"));

        ServiceOrderItem item = new ServiceOrderItem();
        item.setServiceOrder(order);
        item.setServiceLabor(labor);

        // Persistir item primeiro para poder associar insumos
        ServiceOrderItem savedItem = repository.save(item);

        // Criar insumos, se houver
        if (request.supplies() != null) {
            for (ServiceOrderItemSupplyRequest supplyRequest : request.supplies()) {
                // Associar o ID do item criado ao request
                ServiceOrderItemSupplyRequest supplyWithItemId = new ServiceOrderItemSupplyRequest(
                        savedItem.getId(),
                        supplyRequest.partStockId(),
                        supplyRequest.quantity()
                );
                supplyService.create(supplyWithItemId);
            }
        }

        return new ServiceOrderItemResponse(savedItem);
    }

    // Alterar item (apenas serviço ou insumos)
    @Transactional
    public ServiceOrderItemResponse update(UUID id, ServiceOrderItemRequest request) {
        validateRequest(request);

        ServiceOrderItem item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        ServiceLabor labor = laborRepository.findById(request.serviceLaborId())
                .orElseThrow(() -> new EntityNotFoundException("Serviço de mão de obra não encontrado"));

        item.setServiceLabor(labor);

        ServiceOrderItem updatedItem = repository.save(item);

        // Atualizar insumos (simplificado: deletar todos e recriar)
        if (request.supplies() != null) {
            if (updatedItem.getSupplies() != null) {
                updatedItem.getSupplies().forEach(s -> supplyService.delete(s.getId()));
            }
            for (ServiceOrderItemSupplyRequest supplyRequest : request.supplies()) {
                ServiceOrderItemSupplyRequest supplyWithItemId = new ServiceOrderItemSupplyRequest(
                        updatedItem.getId(),
                        supplyRequest.partStockId(),
                        supplyRequest.quantity()
                );
                supplyService.create(supplyWithItemId);
            }
        }

        return new ServiceOrderItemResponse(updatedItem);
    }

    // Excluir item
    @Transactional
    public void delete(UUID id) {
        ServiceOrderItem item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        // Excluir todos os insumos do item
        if (item.getSupplies() != null) {
            item.getSupplies().forEach(s -> supplyService.delete(s.getId()));
        }
        //se excluir todos os itens, faz sentido cancelar a ordem de serviço? Ou exclui-la?

        repository.delete(item);
    }

    // Validação do request
    private void validateRequest(ServiceOrderItemRequest request) {
        if (request.serviceOrderId() == null) {
            throw new IllegalArgumentException("O ID da ordem de serviço é obrigatório");
        }
        if (request.serviceLaborId() == null) {
            throw new IllegalArgumentException("O ID do serviço de mão de obra é obrigatório");
        }
    }
}
