package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.*;
import soat_fiap.siaes.domain.partStock.enums.ItemType;

import java.math.BigDecimal;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Entity
@Table(name="tb_items")
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false, precision = 15, scale = 2)
    protected BigDecimal unitPrice;

    @Deprecated
    public Item(){}

    public Item(String name, BigDecimal unitPrice) {
        this.name = name;
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public UUID getId() {
        return id;
    }

    public String getIdAsString() {
        return id.toString();
    }

    public abstract ItemType getType();
}
