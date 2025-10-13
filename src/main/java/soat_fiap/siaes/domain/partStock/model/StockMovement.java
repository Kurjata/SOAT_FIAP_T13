package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.*;
import lombok.*;

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
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType type;

    @Column(nullable = false)
    private Integer quantity; // quantidade movimentada

    @Column(nullable = false)
    private Integer balanceBefore; // saldo antes da movimentação

    @Column(nullable = false)
    private Integer balanceAfter; // saldo após movimentação

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalValue;

    private UUID orderId;

    //private String observation;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
