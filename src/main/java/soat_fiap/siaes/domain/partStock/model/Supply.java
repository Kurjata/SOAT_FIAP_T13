package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import soat_fiap.siaes.domain.partStock.enums.ItemType;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("supply")
public class Supply extends Item{

    private String supplier;
    private Boolean available;

    @Deprecated
    public Supply(){}

    public Supply(String name, BigDecimal unitPrice, UnitMeasure unitMeasure, String supplier, Boolean available) {
        super(name, unitPrice, unitMeasure);
        this.supplier = supplier;
        this.available = available;
    }

    public String getSupplier() {
        return supplier;
    }

    public void update(String name, BigDecimal unitPrice,String supplier) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.supplier = supplier;
    }

    @Override
    public ItemType getType() {
        return ItemType.SUPPLY;
    }
}
