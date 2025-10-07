package soat_fiap.siaes.domain.serviceOrderItemSupply.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soat_fiap.siaes.domain.partStock.model.Item;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tb_activity_item")
public class ActivityItem {
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
    private Integer quantity; // quantidade utilizada

    @Column(nullable = false)
    private BigDecimal unitPrice; // preço unitário do produto

    public ActivityItem(OrderActivity item, Item part, Integer quantity, BigDecimal unitPrice) {
        this.orderActivity =  item;
        this.partStock = part;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
