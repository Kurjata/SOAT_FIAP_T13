package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "part_stock")
@Data
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

    // Insumos sim ou nao
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean supply;

    //Controla estoque sim ou nao
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean stockControl; // true = stock control enabled


    public PartStock(String ean, String name, Integer stockQuantity, Integer minimumStock, Double unitPrice) {
        this.ean = ean;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
        this.unitPrice = unitPrice;
        this.supply = false;       // default
        this.stockControl = false; // default
    }


    public PartStock(String ean, String name, int stockQuantity, int minimumStock,Double unitPrice ,boolean supply,boolean stockControl) {
        this.ean = ean;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.minimumStock = minimumStock;
        this.unitPrice = unitPrice;
        this.supply = supply;
        this.stockControl = supply;
    }

    public String getIdAsString() {
        return id.toString();
    }
}


