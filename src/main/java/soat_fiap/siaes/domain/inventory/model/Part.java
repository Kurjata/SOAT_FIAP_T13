package soat_fiap.siaes.domain.inventory.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.springframework.util.Assert;
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

    public void addStock(int quantityToAdd) {
        validatePositiveQuantity(quantityToAdd);
        this.quantity += quantityToAdd;
    }

    public void removeStock(int quantityToRemove) {
        validatePositiveQuantity(quantityToRemove);
        if (!hasAvailableStock(quantityToRemove)) {
            throw new BusinessException(String.format("Estoque insuficiente para remover. Disponível: %d, Solicitado: %d", this.quantity, quantityToRemove));
        }

        this.quantity -= quantityToRemove;
        checkMinimumStockAlert();
    }

    public void adjustStock(int adjustment) {
        int newStock = this.quantity + adjustment;
        if (newStock < 0) throw new BusinessException("Ajuste resultaria em estoque negativo");

        this.quantity = newStock;
        checkMinimumStockAlert();
    }

    private void addReserved(int quantityToReserved){
        validatePositiveQuantity(quantityToReserved);
        this.reservedQuantity += quantityToReserved;
    }

    private void removeReserved(int quantityToReserved) {
        validatePositiveQuantity(quantityToReserved);

        if (!hasAvailableReservedStock(quantityToReserved)) {
            throw new BusinessException(String.format("Estoque insuficiente para remover. Disponível: %d, Solicitado: %d", this.reservedQuantity, quantityToReserved));
        }

        this.reservedQuantity -= quantityToReserved;
    }

    public void consumeReserved(int quantityToConsume){
        removeReserved(quantityToConsume);
    }

    public void moveToReserved(int quantity) {
        removeStock(quantity);
        addReserved(quantity);
    }

    public void moveToAvailable(int quantity) {
        removeReserved(quantity);
        addStock(quantity);
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

    public boolean hasAvailableStock(int requiredQuantity) {
        return this.quantity >= requiredQuantity;
    }

    public boolean hasAvailableReservedStock(int requiredQuantity) {
        return this.reservedQuantity >= requiredQuantity;
    }

    public boolean isBelowMinimumStock() {
        return minimumStockQuantity != null && this.quantity < minimumStockQuantity;
    }

    private void checkMinimumStockAlert() {
        if (isBelowMinimumStock()) {
            //domainEvents.add(new StockBelowMinimumEvent(this));
        }
    }

    private void validatePositiveQuantity(int quantity) {
        Assert.isTrue(quantity > 0, "Quantidade deve ser maior que zero");
    }
}
