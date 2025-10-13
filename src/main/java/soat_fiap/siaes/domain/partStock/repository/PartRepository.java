package soat_fiap.siaes.domain.partStock.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import soat_fiap.siaes.domain.partStock.model.Part;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartRepository {
    Part save(Part part);
    Page<Part> findAll(Pageable pageable);
    Optional<Part> findById(UUID id);
    Optional<Part> findByEan(String ean);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    boolean existsByEan(String ean);
    boolean existsByEanAndIdNot(String ean, UUID id);

    @Query("SELECT p FROM Part p WHERE p.quantity < p.minimumStockQuantity")
    List<Part> findAllBelowMinimumStock();
}
