package soat_fiap.siaes.domain.serviceOrderItemSupply.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.partStock.model.PartStock;
import soat_fiap.siaes.domain.partStock.repository.PartStockRepository;
import soat_fiap.siaes.domain.serviceOrderItem.model.ServiceOrderItem;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ServiceOrderItemSupply;
import soat_fiap.siaes.infrastructure.persistence.serviceOrderItem.ServiceOrderItemRepository;
import soat_fiap.siaes.infrastructure.persistence.serviceOrderItemSupply.ServiceOrderItemSupplyRepository;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyRequest;
import soat_fiap.siaes.interfaces.serviceOrderItemSupply.dto.ServiceOrderItemSupplyResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ServiceOrderItemSupplyService {
    private final ServiceOrderItemSupplyRepository repository;
    private final ServiceOrderItemRepository itemRepository;
    private final PartStockRepository partStockRepository;

    // Listar insumos de um item
    public List<ServiceOrderItemSupplyResponse> findByServiceOrderItem(UUID itemId) {
        ServiceOrderItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));
        return item.getSupplies().stream()
                .map(ServiceOrderItemSupplyResponse::new)
                .collect(Collectors.toList());
    }

    // Consultar insumo específico
    public ServiceOrderItemSupplyResponse findById(UUID id) {
        ServiceOrderItemSupply supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));
        return new ServiceOrderItemSupplyResponse(supply);
    }

    // Criar insumo
    @Transactional
    public ServiceOrderItemSupplyResponse create(ServiceOrderItemSupplyRequest request) {
        validateRequest(request);

        PartStock partStock = partStockRepository.findById(request.partStockId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        ServiceOrderItem item = itemRepository.findById(request.serviceOrderItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item da ordem não encontrado"));

        ServiceOrderItemSupply supply = new ServiceOrderItemSupply();
        supply.setServiceOrderItem(item);
        supply.setPartStock(partStock);
        supply.setQuantity(request.quantity());
        supply.setUnitPrice(new BigDecimal(partStock.getUnitPrice()));

        ServiceOrderItemSupply saved = repository.save(supply);
        return new ServiceOrderItemSupplyResponse(saved);
    }

    // Atualizar insumo
    @Transactional
    public ServiceOrderItemSupplyResponse update(UUID id, ServiceOrderItemSupplyRequest request) {
        validateRequest(request);

        ServiceOrderItemSupply supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        PartStock partStock = partStockRepository.findById(request.partStockId())
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));

        supply.setPartStock(partStock);
        supply.setQuantity(request.quantity());
        supply.setUnitPrice(new BigDecimal(partStock.getUnitPrice()));

        ServiceOrderItemSupply updated = repository.save(supply);
        return new ServiceOrderItemSupplyResponse(updated);
    }

    // Excluir insumo
    @Transactional
    public void delete(UUID id) {
        ServiceOrderItemSupply supply = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado"));
        repository.delete(supply);
    }

    // Validação de request
    private void validateRequest(ServiceOrderItemSupplyRequest request) {
        if (request.partStockId() == null) {
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
