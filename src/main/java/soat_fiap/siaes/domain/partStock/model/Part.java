package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import soat_fiap.siaes.domain.partStock.enums.ItemType;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("part")
public class Part extends Item {

    private Integer quantity;
    private String ean;
    private String manufacturer;
    private Integer minimumStockQuantity;

    @Deprecated
    public Part(){}

    public Part(String name, BigDecimal unitPrice, Integer quantity, String ean , String manufacturer, Integer minimumStockQuantity){
        super(name, unitPrice);
        this.quantity = quantity;
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

    @Override
    public ItemType getType() {
        return ItemType.PART;
    }
}
