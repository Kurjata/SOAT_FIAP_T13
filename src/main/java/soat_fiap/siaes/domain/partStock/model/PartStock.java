package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "part_stock")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartStock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "part_stock_id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private String ean;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer minimumStock;

    @Column(nullable = false)
    private Double unitPrice;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean supply;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean stockControl;

    public PartStock(String ean, String name, Integer stockQuantity, Integer minimumStock, Double unitPrice) {
        this.ean = ean;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
        this.unitPrice = unitPrice;
        this.supply = false;
        this.stockControl = false;
    }


    public PartStock(String ean, String name, int stockQuantity, int minimumStock,Double unitPrice ,boolean supply,boolean stockControl) {
        this.ean = ean;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
        this.unitPrice = unitPrice;
        this.supply = supply;
        this.stockControl = stockControl;
    }

    public String getIdAsString() {
        return id.toString();
    }
}


