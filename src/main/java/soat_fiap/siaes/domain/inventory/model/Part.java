package soat_fiap.siaes.domain.inventory.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import soat_fiap.siaes.domain.inventory.enums.ItemType;
import soat_fiap.siaes.shared.BusinessException;

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

    public void add(Integer amount) {
        if (amount == null || amount <= 0)
            throw new IllegalArgumentException("Quantidade deve ser maior que zero para adicionar ao estoque.");

        this.quantity = (this.quantity == null ? 0 : this.quantity) + amount;
    }

    public void remove(Integer quantity) {
        this.quantity = safeSubtract(this.quantity, quantity);
    }

    public void addReserved(Integer quantity){
        this.reservedQuantity += quantity;
    }

    public void removeReserved(Integer quantity) {
        this.reservedQuantity = safeSubtract(this.reservedQuantity, quantity);
    }

    private int safeSubtract(Integer current, Integer amount) {
        if (current == null) current = 0;
        if (amount == null) amount = 0;
        return Math.max(0, current - amount);
    }

    public void adjustStock(Integer quantity) {
        if (quantity == null) throw new IllegalArgumentException("Quantidade deve ser informada.");
        int newStock = (this.quantity != null ? this.quantity : 0) + quantity;
        if (newStock < 0) throw new IllegalArgumentException("O estoque não pode ficar negativo.");
        this.quantity = newStock;
    }

    public void removeStock(int quantity, boolean shouldRemoveReserved) {
        if (shouldRemoveReserved) {
            removeReserved(quantity);
            return;
        }

        if (getQuantity() < quantity) {
            throw new BusinessException("Estoque insuficiente para " + getName());
        }

        remove(quantity);
        addReserved(quantity);
    }

    public void addStock(int quantity) {
        add(quantity);
        removeReserved(quantity);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public void setMinimumStockQuantity(Integer minimumStockQuantity) {
        this.minimumStockQuantity = minimumStockQuantity;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void setUnitPrice(BigDecimal unitPrice) {
        super.setUnitPrice(unitPrice);
    }

    @Override
    public void setUnitMeasure(UnitMeasure unitMeasure) {
        super.setUnitMeasure(unitMeasure);
    }
}
