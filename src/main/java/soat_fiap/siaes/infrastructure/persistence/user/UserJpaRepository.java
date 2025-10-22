package soat_fiap.siaes.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.user.model.document.Document;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepository {
    Optional<User> findByLogin(String login);
    boolean existsByLoginOrDocument(String login, Document document);
    Optional<User> findByDocument(Document document);
    boolean existsByDocument(Document document);
}
