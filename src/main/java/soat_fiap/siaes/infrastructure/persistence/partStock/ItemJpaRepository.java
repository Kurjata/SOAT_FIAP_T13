package soat_fiap.siaes.infrastructure.persistence.partStock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.partStock.model.Item;
import soat_fiap.siaes.domain.partStock.repository.ItemRepository;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Integer>, ItemRepository {
}
