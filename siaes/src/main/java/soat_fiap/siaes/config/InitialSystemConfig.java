package soat_fiap.siaes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import soat_fiap.siaes.domain.entity.User;
import soat_fiap.siaes.domain.enums.RoleEnum;
import soat_fiap.siaes.domain.repository.UserRepository;

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
        // Cria usuário admin se não existir
        createUserIfNotExist("admin", "Administrator", "admin", RoleEnum.ADMIN);
        createUserIfNotExist("collaborator", "Collaborator da silva", "collaborator", RoleEnum.COLLABORATOR);
    }

    private void createUserIfNotExist(String login, String name, String password, RoleEnum role) {
        userRepository.findByLogin(login)
                .orElseGet(() -> {
                    User user = new User();
                    user.setName(name);
                    user.setLogin(login);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setRole(role);
                    return userRepository.save(user);
                });
    }
}
