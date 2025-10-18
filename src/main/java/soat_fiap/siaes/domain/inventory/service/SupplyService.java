package soat_fiap.siaes.domain.inventory.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.inventory.model.Supply;
import soat_fiap.siaes.domain.inventory.repository.SupplyRepository;
import soat_fiap.siaes.interfaces.inventory.dto.UpdateSupplyRequest;

import java.util.UUID;

@Service
public class SupplyService {

    private final SupplyRepository supplyRepository;

    public SupplyService(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    public Supply save(Supply supply) {
        return supplyRepository.save(supply);
    }

    public Page<Supply> findAll(Pageable pageable) {
        return supplyRepository.findAll(pageable);
    }

    public Supply findById(UUID id) {
        return supplyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Insumo não encontrado com ID: " + id));
    }

    public Supply update(UUID id, UpdateSupplyRequest request) {
        Supply existing = findById(id);

        existing.setName(request.name());
        existing.setUnitPrice(request.unitPrice());
        existing.setUnitMeasure(request.unitMeasure());
        existing.setSupplier(request.supplier());
        existing.setAvailability(request.available());

        return supplyRepository.save(existing);
    }

    public void deleteById(UUID id) {
        if (!supplyRepository.existsById(id)) {
            throw new EntityNotFoundException("Insumo não encontrado com ID: " + id);
        }
        supplyRepository.deleteById(id);
    }

    public Supply updateAvailability(UUID id, Boolean available) {
        Supply supply = findById(id);
        supply.setAvailability(available);
        return supplyRepository.save(supply);
    }
}
