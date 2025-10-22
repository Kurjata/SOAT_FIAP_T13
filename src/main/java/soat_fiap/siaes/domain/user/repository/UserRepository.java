package soat_fiap.siaes.domain.user.repository;

import org.springframework.data.domain.Page;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.model.document.Document;

import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository  {
    User save(User user);
    Optional<User> findByLogin(String login);
    boolean existsByLoginOrDocument(String login, Document document);
    Optional<User> findByDocument(Document document);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    Optional<User> findById(UUID id);
    Page<User> findAll(Pageable pageable);
    boolean existsByDocument(Document document);
}