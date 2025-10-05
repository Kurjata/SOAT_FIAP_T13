package soat_fiap.siaes.infrastructure.persistence.serviceOrderToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.serviceOrderToken.model.ServiceOrderToken;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceOrderTokenRepository extends JpaRepository<ServiceOrderToken, UUID> {
    Optional<ServiceOrderToken> findByToken(String token);
}
