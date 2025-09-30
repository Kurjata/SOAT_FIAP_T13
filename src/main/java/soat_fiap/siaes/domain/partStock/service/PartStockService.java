package soat_fiap.siaes.domain.partStock.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.partStock.event.StockBelowMinimumEvent;
import soat_fiap.siaes.domain.partStock.model.MovementType;
import soat_fiap.siaes.domain.partStock.model.PartStock;
import soat_fiap.siaes.domain.partStock.repository.PartStockRepository;
import soat_fiap.siaes.interfaces.partStock.dto.UpdatePartStockRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartStockService {
    private final PartStockRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final StockMovementService stockMovementService;

       @Transactional(readOnly = true)
    public Page<PartStock> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public PartStock findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada com ID: " + id));
    }

    @Transactional
    public PartStock save(PartStock partStock) {
        if (repository.existsByEan(partStock.getEan())) {
            throw new IllegalArgumentException("EAN já existe: " + partStock.getEan());
        }

        if (!partStock.isStockControl()) {
            partStock.setStockQuantity(0);
            partStock.setMinimumStock(0);
        }

        return repository.save(partStock);
    }

    @Transactional
    public PartStock update(UUID id, UpdatePartStockRequest request) {
        PartStock existing = this.findById(id);

        // Validação EAN único
        if (repository.existsByEanAndIdNot(request.ean(), id)) {
            throw new IllegalArgumentException("Já existe outra peça com o EAN: " + request.ean());
        }

        existing.setEan(request.ean());
        existing.setName(request.name());
        existing.setUnitPrice(request.unitPrice());
        existing.setSupply(request.supply());
        existing.setStockControl(request.stockControl());

        if (request.stockControl()) {
            existing.setStockQuantity(request.stockQuantity());
            existing.setMinimumStock(request.minimumStock());

            if (request.stockQuantity() < request.minimumStock()) {
                eventPublisher.publishEvent(new StockBelowMinimumEvent(existing));
            }
        } else {
            existing.setStockQuantity(0);
            existing.setMinimumStock(0);
        }

        return repository.save(existing);
    }

    @Transactional
    public void deleteById(UUID id) {
        PartStock existing = this.findById(id);
        repository.delete(existing);
    }

    @Transactional
    public PartStock consumeStock(UUID id, int quantity, UUID orderId) {
        PartStock part = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada com ID: " + id));

        if (!part.isStockControl()) {
            stockMovementService.registerMovement(
                    id,
                    MovementType.SAIDA_OS,
                    quantity,
                    orderId
            );
            return part;
        }

        if (part.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Estoque insuficiente para a peça: " + part.getName());
        }

        part.setStockQuantity(part.getStockQuantity() - quantity);

        if (part.getStockQuantity() < part.getMinimumStock()) {
            eventPublisher.publishEvent(new StockBelowMinimumEvent(part));
        }

        PartStock updated = repository.save(part);

        stockMovementService.registerMovement(
                id,
                MovementType.SAIDA_OS,
                quantity,
                orderId
        );

        return updated;
    }

    @Transactional
    public PartStock addStock(UUID id, int quantity) {
        PartStock part = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada com ID: " + id));

        if (!part.isStockControl()) {
            stockMovementService.registerMovement(
                    id,
                    MovementType.ENTRADA,
                    quantity,
                    null
            );
            return part;
        }

        part.setStockQuantity(part.getStockQuantity() + quantity);
        PartStock updated = repository.save(part);

        stockMovementService.registerMovement(
                id,
                MovementType.ENTRADA,
                quantity,
                null
        );

        return updated;
    }

}
