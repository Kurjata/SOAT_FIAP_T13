package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.*;
import soat_fiap.siaes.domain.partStock.enums.ItemType;

import java.math.BigDecimal;
import java.util.UUID;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Entity
@Table(name="items")
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false, precision = 15, scale = 2)
    protected BigDecimal unitPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected UnitMeasure unitMeasure;

    @Deprecated
    public Item(){}

    public Item(String name, BigDecimal unitPrice, UnitMeasure unitMeasure) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.unitMeasure = unitMeasure;
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

    public UnitMeasure getUnitMeasure() {
        return unitMeasure;
    }

    public String getIdAsString() {
        return id.toString();
    }

    public abstract ItemType getType();
}
