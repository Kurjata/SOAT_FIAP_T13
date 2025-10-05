package soat_fiap.siaes.infrastructure.persistence.serviceOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrder.model.ServiceOrder;


import java.util.UUID;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, UUID> {
    // Busca ordens pelo document do usuário (CPF ou CNPJ)
    @Query(value = "SELECT * FROM tb_service_order o " +
            "JOIN tb_users u ON o.user_id = u.user_id " +
            "WHERE u.document = :cpfCnpj",
            countQuery = "SELECT count(*) FROM tb_service_order o " +
                    "JOIN tb_users u ON o.user_id = u.user_id " +
                    "WHERE u.document = :cpfCnpj",
            nativeQuery = true)
    Page<ServiceOrder> findByUserDocumentValue(@Param("cpfCnpj") String cpfCnpj, Pageable pageable);

    // Busca ordens pelo id do usuário
    Page<ServiceOrder> findByUserId(UUID userId, Pageable pageable);
    // Busca ordens pelo id do veiculo
    Page<ServiceOrder> findByVehicleId(UUID vehicleId, Pageable pageable);
    // Busca ordens pelo plate do veiculo
    Page<ServiceOrder> findByVehiclePlateIgnoreCase(String plate, Pageable pageable);
}
