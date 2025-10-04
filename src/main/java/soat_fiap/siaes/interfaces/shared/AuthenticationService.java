package soat_fiap.siaes.interfaces.shared;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.interfaces.user.dto.LoginRequest;
import soat_fiap.siaes.interfaces.user.dto.LoginResponse;
import soat_fiap.siaes.interfaces.user.dto.MessageDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Serviço responsável pela autenticação de usuários e geração/validação de JWT.
 */
@Slf4j
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Algorithm algorithm;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 @Value("${api.security.token.secret}") String secret) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.algorithm = Algorithm.HMAC256(secret);
    }

    /**
     * Carrega o usuário pelo login para o Spring Security.
     *
     * @param login login do usuário
     * @return UserDetails para autenticação
     * @throws UsernameNotFoundException se o usuário não for encontrado
     */
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        log.info("Carregando usuário pelo login: {}", login);
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + login));

        log.info("Usuário {} encontrado com sucesso", login);
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .build();
    }

    /**
     * Autentica o usuário e gera token e refresh token.
     *
     * @param loginRequest dados de login
     * @return ResponseEntity com LoginResponse ou mensagem de erro
     */
    public ResponseEntity<?> authenticate(LoginRequest loginRequest) {
        String login = loginRequest.login();
        try {
            log.info("Autenticando usuário: {}", login);

            User user = userRepository.findByLogin(login)
                    .orElseThrow(() -> {
                        log.warn("Autenticação falhou: usuário {} não encontrado", login);
                        return new BadCredentialsException("Usuário ou senha inválidos");
                    });

            if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
                log.warn("Autenticação falhou: senha inválida para user: {}", login);
                throw new BadCredentialsException("Usuário ou senha inválidos");
            }

            String token = generateToken(user);
            String refreshToken = generateRefreshToken(user);
            log.info("Usuário {} autenticado com sucesso", login);

            LoginResponse response = new LoginResponse(token, refreshToken, user.getName(), user.getRole());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDTO("Usuário ou senha inválidos"));
        } catch (Exception ex) {
            log.error("Erro inesperado ocorreu durante autenticação do usuário {}: {}", login, ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageDTO("Erro interno ao autenticar usuário"));
        }
    }

    /**
     * Gera um JWT de acesso válido por 8 horas.
     *
     * @param user usuário autenticado
     * @return token JWT
     */
    public String generateToken(User user) {
        try {
            log.debug("Generating JWT token for user: {}", user.getLogin());
            return JWT.create()
                    .withIssuer("siaes-api")
                    .withSubject(user.getLogin())
                    .withClaim("login", user.getLogin())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(generateExpiryDate())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Erro ao gerar o JWT token para o usuário: {}: {}", user.getLogin(), e.getMessage());
            throw new IllegalStateException("Erro ao gerar token JWT");
        }
    }

    /**
     * Gera um JWT de refresh token válido por 30 dias.
     *
     * @param user usuário autenticado
     * @return refresh token JWT
     */
    public String generateRefreshToken(User user) {
        try {
            log.debug("Gerando token de atualização para o usuário: {}", user.getLogin());
            return JWT.create()
                    .withIssuer("siaes-api")
                    .withSubject(user.getLogin())
                    .withClaim("login", user.getLogin())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00")))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Erro ao gerar token de atualização para o usuário: {}: {}", user.getLogin(), e.getMessage());
            throw new IllegalStateException("Erro ao gerar token de atualização");
        }
    }

    /**
     * Valida um JWT e retorna o login do usuário.
     *
     * @param token token JWT
     * @return login do usuário
     * @throws IllegalArgumentException se o token for inválido ou expirado
     */
    public String validateToken(String token) {
        try {
            log.debug("Validando JWT token");
            return JWT.require(algorithm)
                    .withIssuer("siaes-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            log.warn("Inválido JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Token inválido ou expirado");
        }
    }

    /**
     * Gera a data de expiração do token de acesso (8 horas a partir do momento atual).
     *
     * @return Instant de expiração
     */
    private Instant generateExpiryDate() {
        return LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("-03:00"));
    }
}
