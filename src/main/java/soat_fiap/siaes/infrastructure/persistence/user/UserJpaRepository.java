package soat_fiap.siaes.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.interfaces.user.document.Document;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepository {
    Optional<User> findByLogin(String login);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM tb_users u
                WHERE u.login = :login
                    OR u.document = :document
            ) AS user_exists;
            """, nativeQuery = true)
    boolean existsUserByLoginOrDocument(String login, String document);

    Optional<User> findByDocument(Document document);
}
