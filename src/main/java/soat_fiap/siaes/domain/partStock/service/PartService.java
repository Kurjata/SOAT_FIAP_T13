package soat_fiap.siaes.domain.partStock.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.partStock.model.MovementType;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.repository.PartRepository;
import soat_fiap.siaes.interfaces.partStock.dto.UpdatePartRequest;


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
        Part existing = partRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada com ID: " + id));

        request.applyTo(existing);

        return partRepository.save(existing);
    }

    public void deleteById(UUID id) {
        if (!partRepository.existsById(id)) {
            throw new EntityNotFoundException("Peça não encontrada com ID: " + id);
        }
        partRepository.deleteById(id);
    }

    public List<Part> findAllBelowMinimumStock() {
        return partRepository.findAllBelowMinimumStock();
    }

     public Part addStock(UUID id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new EntityNotFoundException("Quantidade inválida para adicionar ao estoque.");
        }

        Part part = findById(id);
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada com ID: " + id));

        part.add(quantity);
         Part updated = partRepository.save(part);

         stockMovementService.registerMovement(part, MovementType.ENTRADA, quantity);
         return updated;
    }


    public Part updateStockQuantity(UUID id, Integer quantity) {

        Part part = findById(id);

        if (quantity == null) {
            throw new IllegalArgumentException("A quantidade deve ser informada.");
        }

        int currentStock = part.getQuantity() != null ? part.getQuantity() : 0;
        int newStock = currentStock + quantity;

        if (newStock < 0) {
            throw new IllegalArgumentException("O valor do ajuste não pode deixar o estoque negativo. Estoque atual: "
                            + currentStock + ", tentativa de ajuste: " + quantity
            );
        }

        part.add(quantity);
        Part updated = partRepository.save(part);

        stockMovementService.registerMovement(part, MovementType.AJUSTE, quantity);
        return updated;
    }

}
