package soat_fiap.siaes.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.interfaces.user.dto.CreateUserRequest;

import java.util.List;

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
}