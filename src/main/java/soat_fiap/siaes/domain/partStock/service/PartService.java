package soat_fiap.siaes.domain.partStock.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.partStock.model.Part;
import soat_fiap.siaes.domain.partStock.repository.PartRepository;
import soat_fiap.siaes.interfaces.partStock.dto.UpdatePartRequest;


import java.util.UUID;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
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

}
