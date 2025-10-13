package soat_fiap.siaes.domain.partStock.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.model.Supply;
import soat_fiap.siaes.domain.partStock.repository.SupplyRepository;
import soat_fiap.siaes.interfaces.partStock.dto.UpdateSupplyRequest;

import java.util.UUID;

@Service
public class SupplyService {

    private final SupplyRepository supplyRepository;

    public SupplyService(SupplyRepository supplyRepository) {this.supplyRepository = supplyRepository;}

    public Supply save(Supply supply){ return supplyRepository.save(supply);}

    public Page<Supply> findAll(Pageable pageable) {
        return supplyRepository.findAll(pageable);
    }

    public Supply findById(UUID id) {
        return supplyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrada com ID: " + id));
    }

    public Supply update(UUID id, UpdateSupplyRequest request) {
        Supply existing = supplyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado com ID: " + id));

        request.applyToSupply(existing);

        return supplyRepository.save(existing);
    }

    public void deleteById(UUID id) {
        if (!supplyRepository.existsById(id)) {
            throw new EntityNotFoundException("Serviço não encontrado com ID: " + id);
        }
        supplyRepository.deleteById(id);
    }

    public Supply updateAvailability(UUID id, Boolean available) {
        Supply existingSupply = supplyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suprimento não encontrado com ID: " + id));

        if (Boolean.TRUE.equals(available)) {
            existingSupply.enable();
        } else {
            existingSupply.disable();
        }

        return supplyRepository.save(existingSupply);
    }

}
