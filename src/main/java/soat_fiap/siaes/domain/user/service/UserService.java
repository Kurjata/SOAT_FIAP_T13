package soat_fiap.siaes.domain.user.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.user.model.document.DocumentFactory;
import soat_fiap.siaes.interfaces.user.dto.CreateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UpdateUserRequest;

import org.springframework.data.domain.Pageable;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User save(CreateUserRequest userRequest) {
        if (userRepository.existsByLoginOrDocument(userRequest.login(), DocumentFactory.fromString(userRequest.document()))) {
            throw new IllegalArgumentException("Documento " + userRequest.document() + " ou login " + userRequest.login() + " já cadastrado");
        }

        User user = userRequest.toModel(passwordEncoder);

        return userRepository.save(user);
    }

    public User findByDocument(String document) {
        return userRepository.findByDocument(DocumentFactory.fromString(document))
                .orElseThrow(() -> new EntityNotFoundException("Usuário com documento " + document + " não encontrado"));
    }

    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        userRepository.deleteById(id);
    }

    public User update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (userRepository.existsByDocument(DocumentFactory.fromString(request.document()))) {
            throw new IllegalArgumentException("Documento já cadastrado");
        }

        user.setName(request.name());
        user.setDocument(DocumentFactory.fromString(request.document()));
        user.setRole(request.role());

        return userRepository.save(user);
    }
}