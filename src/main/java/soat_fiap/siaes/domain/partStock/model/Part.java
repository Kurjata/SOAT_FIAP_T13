package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import soat_fiap.siaes.domain.partStock.enums.ItemType;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("part")
public class Part extends Item {

    private Integer quantity;
    private Integer reservedQuantity;
    private Integer minimumStockQuantity;
    private String ean;
    private String manufacturer;

    @Deprecated
    public Part(){}

    public Part(String name, BigDecimal unitPrice, UnitMeasure unitMeasure, Integer quantity, Integer reservedQuantity, String ean , String manufacturer, Integer minimumStockQuantity){
        super(name, unitPrice, unitMeasure);
        this.quantity = quantity;
        this.reservedQuantity = reservedQuantity;
        this.ean = ean;
        this.manufacturer = manufacturer;
        this.minimumStockQuantity = minimumStockQuantity;
    }

    public void update(String name, BigDecimal unitPrice, Integer quantity,
                       String ean, String manufacturer, Integer minimumStockQuantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.ean = ean;
        this.manufacturer = manufacturer;
        this.minimumStockQuantity = minimumStockQuantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getEan() {
        return ean;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Integer getMinimumStockQuantity() {
        return minimumStockQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    @Override
    public ItemType getType() {
        return ItemType.PART;
    }

    public void add(Integer quantity){
        this.quantity += quantity;
    }

    public void minus(Integer quantity) {
        this.quantity = safeSubtract(this.quantity, quantity);
    }

    public void addReserved(Integer quantity){
        this.reservedQuantity += quantity;
    }

    public void minusReserved(Integer quantity) {
        this.reservedQuantity = safeSubtract(this.reservedQuantity, quantity);
    }

    private int safeSubtract(Integer current, Integer amount) {
        if (current == null) current = 0;
        if (amount == null) amount = 0;
        return Math.max(0, current - amount);
    }
}
