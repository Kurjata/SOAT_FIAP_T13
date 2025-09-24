package soat_fiap.siaes.domain.user.repository;

import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.interfaces.user.document.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository  {
    List<User> findAll();
    User save(User user);
    Optional<User> findByLogin(String login);
    boolean existsUserByLoginOrDocument(String login, String document);
    Optional<User> findByDocument(Document document);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    Optional<User> findById(UUID id);
}