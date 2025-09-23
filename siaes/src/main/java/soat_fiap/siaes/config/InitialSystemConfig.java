package soat_fiap.siaes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.repository.UserRepository;

@Configuration
public class InitialSystemConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialSystemConfig(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        createUserIfNotExist("admin", "Administrator", "admin", RoleEnum.ADMIN, "239.413.650-29");
        createUserIfNotExist("collaborator", "Collaborator da silva", "collaborator", RoleEnum.COLLABORATOR, "27.295.338/0001-74");
    }

    private void createUserIfNotExist(String login, String name, String password, RoleEnum role, String document) {
        userRepository.findByLogin(login)
                .orElseGet(() -> {
                    User user = new User(name, login, passwordEncoder.encode(password), role, document);
                    return userRepository.save(user);
                });
    }
}
