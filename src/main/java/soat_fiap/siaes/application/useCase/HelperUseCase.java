package soat_fiap.siaes.application.useCase;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;


@Component
public class HelperUseCase {

    private final UserRepository userRepository;

    public HelperUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User carregarUsuarioEximioJWT() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        String username = authentication.getName();

        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    public RoleEnum getRoleUsuarioLogado() {
        return carregarUsuarioEximioJWT().getRole();
    }
}