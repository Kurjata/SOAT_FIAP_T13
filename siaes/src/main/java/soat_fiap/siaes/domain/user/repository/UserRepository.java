package soat_fiap.siaes.domain.user.repository;

import soat_fiap.siaes.domain.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository  {
    List<User> findAll();
    User save(User user);
    Optional<User> findByLogin(String login);
    boolean existsUserByLoginOrDocument(String login, String document);
}