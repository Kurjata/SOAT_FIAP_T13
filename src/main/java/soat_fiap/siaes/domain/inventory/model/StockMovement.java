package soat_fiap.siaes.domain.inventory.model;

import jakarta.persistence.*;
import lombok.*;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock_movements")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer balanceBefore;

    @Column(nullable = false)
    private Integer balanceAfter;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalValue;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private ServiceOrder serviceOrder;

    public StockMovement(Part part, MovementType type, Integer quantity, Integer balanceBefore, Integer balanceAfter) {
        this.part = part;
        this.type = type;
        this.quantity = quantity;
        this.balanceBefore = balanceBefore;
        this.balanceAfter = balanceAfter;
        this.unitPrice = part.getUnitPrice();
        this.totalValue = calculateTotalPrice();
    }

    public BigDecimal calculateTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
