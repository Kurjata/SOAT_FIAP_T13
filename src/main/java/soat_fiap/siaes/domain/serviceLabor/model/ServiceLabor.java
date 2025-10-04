package soat_fiap.siaes.domain.serviceLabor.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tb_service_labor")
public class ServiceLabor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String description;   // (ex.: "Troca de óleo", "Alinhamento")

    @Column(nullable = false)
    private BigDecimal laborCost; // Custo da mão de obra
}
