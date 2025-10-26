package soat_fiap.siaes.domain.serviceOrder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soat_fiap.siaes.domain.inventory.model.Item;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "service_order_item_id", nullable = false)
    private OrderActivity orderActivity;

    @ManyToOne
    @JoinColumn(name = "part_stock_id", nullable = false)
    private Item partStock; //Insumo

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Deprecated
    public OrderItem() {}

    public OrderItem(OrderActivity orderActivity, Item part, Integer quantity, BigDecimal unitPrice) {
        this.orderActivity =  orderActivity;
        this.partStock = part;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public UUID getId() {
        return id;
    }

    public String getIdAsString() {
        return id.toString();
    }

    public OrderActivity getOrderActivity() {
        return orderActivity;
    }

    public Item getPartStock() {
        return partStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setPartStock(Item partStock) {
        this.partStock = partStock;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
