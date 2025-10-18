package soat_fiap.siaes.domain.serviceLabor.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "service_labors")
public class ServiceLabor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String description;

    @Column(nullable = false)
    private BigDecimal laborCost;

    @Deprecated
    public ServiceLabor() {}

    public  ServiceLabor(String description, BigDecimal laborCost) {
        this.description = description;
        this.laborCost = laborCost;
    }

    public String getIdAsString() {
        return id.toString();
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }
}
