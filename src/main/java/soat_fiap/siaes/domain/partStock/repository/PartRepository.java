package soat_fiap.siaes.domain.partStock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.partStock.model.Part;

import java.util.Optional;
import java.util.UUID;

public interface PartRepository {

    Part save(Part part);

    Page<Part> findAll(Pageable pageable);

    Optional<Part> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    boolean existsByEan(String ean);

    boolean existsByEanAndIdNot(String ean, UUID id);
}
