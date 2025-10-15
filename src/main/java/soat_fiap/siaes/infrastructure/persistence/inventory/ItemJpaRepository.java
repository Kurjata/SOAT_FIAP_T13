package soat_fiap.siaes.infrastructure.persistence.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.inventory.model.Item;
import soat_fiap.siaes.domain.inventory.repository.ItemRepository;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Integer>, ItemRepository {
}
