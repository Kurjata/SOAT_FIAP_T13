package soat_fiap.siaes.domain.serviceOrderItem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soat_fiap.siaes.domain.serviceLabor.model.ServiceLabor;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;
import soat_fiap.siaes.domain.serviceOrderItemSupply.model.ServiceOrderItemSupply;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tb_service_order_item")
public class ServiceOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "service_order_id", nullable = false)
    private ServiceOrder serviceOrder;

    @ManyToOne
    @JoinColumn(name = "service_labor_id", nullable = false)
    private ServiceLabor serviceLabor;

    @OneToMany(mappedBy = "serviceOrderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOrderItemSupply> supplies;

    public ServiceOrderItem(ServiceOrder serviceOrder, ServiceLabor serviceLabor) {
        this.serviceOrder = serviceOrder;
        this.serviceLabor = serviceLabor;
    }
}
