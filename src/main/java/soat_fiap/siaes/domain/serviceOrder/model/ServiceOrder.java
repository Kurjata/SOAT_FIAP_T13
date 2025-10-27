package soat_fiap.siaes.domain.serviceOrder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.AbstractAggregateRoot;
import soat_fiap.siaes.application.event.part.UpdateStockEvent;
import soat_fiap.siaes.domain.inventory.enums.StockOperation;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatus;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.Duration.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "service_orders")
public class ServiceOrder extends AbstractAggregateRoot<ServiceOrder> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceOrderStatus orderStatus;

    @ToString.Exclude
    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderActivity> orderActivities;

    public ServiceOrder(User user, Vehicle vehicle, ServiceOrderStatus serviceOrderStatus) {
        this.user = user;
        this.vehicle = vehicle;
        this.orderStatus = serviceOrderStatus;
        this.startTime = LocalDateTime.now();
    }

    public Long getDurationMinutes() {
        if (startTime != null) {
            LocalDateTime effectiveEndTime = (endTime != null) ? endTime : LocalDateTime.now();
            return between(startTime, effectiveEndTime).toMinutes();
        }
        return null;
    }

    public void setUpdateStatus(ServiceOrderStatus status) {
        if (status == ServiceOrderStatus.FINALIZADA) {
            this.endTime = LocalDateTime.now();
        }
        this.orderStatus = status;
    }

    public void updateStatus(ServiceOrderStatus newStatus) {
        setUpdateStatus(newStatus);

        switch (newStatus) {
            case APROVADO_CLIENTE -> registerEvent(new UpdateStockEvent(this, StockOperation.RESERVE_STOCK));
            case REPROVADO_CLIENTE -> registerEvent(new UpdateStockEvent(this, StockOperation.CANCEL_RESERVATION));
            case EM_EXECUCAO -> registerEvent(new UpdateStockEvent(this, StockOperation.CONFIRM_RESERVATION));
        }
    }

    protected List<Object> getDomainEvents() {
        return new ArrayList<>(super.domainEvents());
    }

    public String getIdAsString() {
        return id.toString();
    }
}
