package soat_fiap.siaes.domain.inventory.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import soat_fiap.siaes.domain.inventory.model.Supply;

import java.util.Optional;
import java.util.UUID;

public interface SupplyRepository  {
    Supply save(Supply supply);
    Page<Supply> findAll(Pageable pageable);
    Optional<Supply> findById(UUID id);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
