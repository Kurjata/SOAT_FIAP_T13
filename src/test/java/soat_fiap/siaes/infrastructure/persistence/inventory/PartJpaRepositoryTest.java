package soat_fiap.siaes.infrastructure.persistence.inventory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import soat_fiap.siaes.domain.inventory.model.Part;
import soat_fiap.siaes.domain.inventory.model.UnitMeasure;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PartJpaRepositoryTest {

    @Autowired
    private PartJpaRepository repository;

    @Test
    void findPartsBelowMinimumStock_should_return_only_parts_below_minimum_stock() {
        Part part1 = new Part("Parafuso", BigDecimal.valueOf(5), UnitMeasure.UNIT, 10, 0, "1234567890123", "X", 5);
        Part part2 = new Part("Parafuso 2", BigDecimal.valueOf(5), UnitMeasure.UNIT, 2, 0, "1234567890123", "X", 5);

        repository.saveAll(List.of(part1, part2));

        List<Part> result = repository.findPartsBelowMinimumStock();

        assertThat(result)
                .containsExactly(part2)
                .doesNotContain(part1);
    }
}