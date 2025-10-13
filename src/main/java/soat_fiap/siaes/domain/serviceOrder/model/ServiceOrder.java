package soat_fiap.siaes.domain.serviceOrder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import soat_fiap.siaes.domain.serviceOrder.enums.ServiceOrderStatusEnum;
import soat_fiap.siaes.domain.serviceOrderItem.model.OrderActivity;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.vehicle.model.Vehicle;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tb_service_order")
public class ServiceOrder {
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
    private ServiceOrderStatusEnum orderStatusEnum;

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderActivity> orderActivities;

    public ServiceOrder(User user, Vehicle vehicle, ServiceOrderStatusEnum serviceOrderStatusEnum) {
        this.user = user;
        this.vehicle = vehicle;
        this.orderStatusEnum = serviceOrderStatusEnum;
        this.startTime = LocalDateTime.now();
    }

    public Long getDurationMinutes() {
        if (startTime != null) {
            LocalDateTime effectiveEndTime = (endTime != null) ? endTime : LocalDateTime.now();
            return java.time.Duration.between(startTime, effectiveEndTime).toMinutes();
        }
        return null;
    }

    public void setUpdateStatus(ServiceOrderStatusEnum status) {
        if (status == ServiceOrderStatusEnum.FINALIZADA) {
            this.endTime = LocalDateTime.now();
        }
        this.orderStatusEnum = status;
    }
}
