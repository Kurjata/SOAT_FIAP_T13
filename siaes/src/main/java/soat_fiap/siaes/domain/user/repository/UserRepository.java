package soat_fiap.siaes.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import soat_fiap.siaes.domain.user.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
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
}