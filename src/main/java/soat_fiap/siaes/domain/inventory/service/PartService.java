package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.inventory.model.MovementType;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.repository.PartRepository;
import soat_fiap.siaes.interfaces.inventory.dto.UpdatePartRequest;


import java.util.List;
import java.util.UUID;

@Service
public class PartService {

    private final PartRepository partRepository;
    private final StockMovementService stockMovementService;

    public PartService(PartRepository partRepository, StockMovementService stockMovementService) {
        this.partRepository = partRepository;
        this.stockMovementService = stockMovementService;
    }

    public Part save(Part part) {
        if (partRepository.existsByEan(part.getEan())) {
            throw new IllegalArgumentException("EAN já existe: " + part.getEan());
        }

        return partRepository.save(part);
    }

    public Page<Part> findAll(Pageable pageable) {
        return partRepository.findAll(pageable);
    }

    public Part findById(UUID id) {
        return partRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada com ID: " + id));
    }

    public Part update(UUID id, UpdatePartRequest request) {
        if (partRepository.existsByEanAndIdNot(request.ean(), id))
            throw new IllegalArgumentException("EAN já existe: " + request.ean());

        Part existing = findById(id);
        existing.setName(request.name());
        existing.setUnitPrice(request.unitPrice());
        existing.setUnitMeasure(request.unitMeasure());
        existing.setQuantity(request.quantity());
        existing.setReservedQuantity(request.reservedQuantity());
        existing.setMinimumStockQuantity(request.minimumStockQuantity());
        existing.setEan(request.ean());
        existing.setManufacturer(request.manufacturer());

        return partRepository.save(existing);
    }

    public void deleteById(UUID id) {
        if (!partRepository.existsById(id)) {
            throw new EntityNotFoundException("Peça não encontrada com ID: " + id);
        }
        partRepository.deleteById(id);
    }

    public List<Part> findPartsBelowMinimumStock() {
        return partRepository.findPartsBelowMinimumStock();
    }

    @Transactional
    public Part addStock(UUID id, Integer quantity) {
        Part part = findById(id);
        part.add(quantity);

        Part updated = partRepository.save(part);

        stockMovementService.registerMovement(updated, MovementType.ENTRADA, quantity);
        return updated;
    }

    @Transactional
    public Part updateStockQuantity(UUID id, Integer quantity) {
        Part part = findById(id);
        part.adjustStock(quantity);

        Part updated = partRepository.save(part);

        stockMovementService.registerMovement(part, MovementType.AJUSTE, quantity);
        return updated;
    }

}
