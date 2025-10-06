package soat_fiap.siaes.domain.serviceOrderToken.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_service_order_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "service_order_id", nullable = false)
    private ServiceOrder serviceOrder;

    @Column(nullable = false)
    private LocalDateTime expiration; // Data de expiração do token
}
