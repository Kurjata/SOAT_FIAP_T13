package soat_fiap.siaes.domain.partStock.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.partStock.model.PartStock;
import soat_fiap.siaes.domain.partStock.repository.PartStockRepository;
import soat_fiap.siaes.interfaces.partStock.dto.UpdatePartStockRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartStockService {
    private final PartStockRepository repository;

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
        existing.setStockQuantity(request.stockQuantity());
        existing.setMinimumStock(request.minimumStock());
        existing.setUnitPrice(request.unitPrice());
        existing.setSupply(request.supply());
        existing.setStockControl(request.stockControl());

        return repository.save(existing);
    }

    @Transactional
    public void deleteById(UUID id) {
        PartStock existing = this.findById(id);
        repository.delete(existing);
    }

}
