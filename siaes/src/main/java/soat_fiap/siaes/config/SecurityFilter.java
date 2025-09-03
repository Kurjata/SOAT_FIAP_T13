package soat_fiap.siaes.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import soat_fiap.siaes.domain.entity.User;
import soat_fiap.siaes.domain.repository.UserRepository;
import soat_fiap.siaes.service.AuthenticationService;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public SecurityFilter(AuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);
        if (token != null) {
            try {
                authenticateToken(token);
            } catch (Exception ex) {
                logger.warn("Falha ao autenticar token: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token Bearer do header Authorization.
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * Valida o token e autentica o usuário no contexto do Spring Security.
     */
    private void authenticateToken(String token) {
        String login = authenticationService.validateToken(token);

        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + login));

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("Usuário autenticado: {}", login);
    }
}
