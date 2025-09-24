package soat_fiap.siaes.domain.user.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.interfaces.user.document.DocumentFactory;
import soat_fiap.siaes.interfaces.user.dto.CreateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UpdateUserRequest;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(CreateUserRequest userRequest) {
        if (userRepository.existsUserByLoginOrDocument(userRequest.login(), userRequest.document()))
            throw new IllegalArgumentException();

        User user = userRequest.toModel(passwordEncoder);

        return userRepository.save(user);
    }

    public User findByDocument(String document) {
        return userRepository.findByDocument(DocumentFactory.fromString(document))
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        userRepository.deleteById(id);
    }

    public User update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setName(request.name());
        user.setDocument(DocumentFactory.fromString(request.document()));
        user.setRole(request.role());

        return userRepository.save(user);
    }
}