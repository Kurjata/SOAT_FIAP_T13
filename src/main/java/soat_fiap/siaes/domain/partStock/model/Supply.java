package soat_fiap.siaes.domain.partStock.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("supply")
public class Supply extends Item{

    private String supplier;

    @Deprecated
    public Supply(){}

    public Supply(String name, BigDecimal unitPrice, String supplier) {
        super(name, unitPrice);
        this.supplier = supplier;
    }

    public String getSupplier() {
        return supplier;
    }

    public void update(String name, BigDecimal unitPrice,String supplier) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.supplier = supplier;

    }
}
