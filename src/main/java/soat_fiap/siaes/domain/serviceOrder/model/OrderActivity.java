package soat_fiap.siaes.domain.serviceOrder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "order_activities")
public class OrderActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "service_order_id", nullable = false)
    private ServiceOrder serviceOrder;

    @ManyToOne
    @JoinColumn(name = "service_labor_id", nullable = false)
    private ServiceLabor serviceLabor;

    @OneToMany(mappedBy = "orderActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    public OrderActivity(ServiceOrder serviceOrder, ServiceLabor serviceLabor) {
        this.serviceOrder = serviceOrder;
        this.serviceLabor = serviceLabor;
    }
}
