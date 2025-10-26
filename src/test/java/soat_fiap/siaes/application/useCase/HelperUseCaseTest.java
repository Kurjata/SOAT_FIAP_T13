package soat_fiap.siaes.application.useCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HelperUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private HelperUseCase helperUseCase;

    private User mockUser;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);

        mockUser = mock(User.class);
    }

    private void setupAuthenticatedContext(String username) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
    }

    @Test
    void carregarUsuarioEximioJWT_should_return_user_when_authenticated_and_found() {

        String username = "testuser";
        setupAuthenticatedContext(username);
        when(userRepository.findByLogin(username)).thenReturn(Optional.of(mockUser));

        User result = helperUseCase.carregarUsuarioEximioJWT();

        assertEquals(mockUser, result, "Deve retornar o usuário encontrado.");
        verify(userRepository).findByLogin(username);
    }

    @Test
    void carregarUsuarioEximioJWT_should_throw_RuntimeException_when_not_authenticated() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> helperUseCase.carregarUsuarioEximioJWT(),
                "Deve lançar RuntimeException se não estiver autenticado.");

        assertEquals("Usuário não autenticado.", exception.getMessage());
    }

    @Test
    void carregarUsuarioEximioJWT_should_throw_UsernameNotFoundException_when_user_not_found() {

        String username = "unknownuser";
        setupAuthenticatedContext(username);
        when(userRepository.findByLogin(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> helperUseCase.carregarUsuarioEximioJWT(),
                "Deve lançar UsernameNotFoundException se o usuário não for encontrado.");
    }

    @Test
    void carregarUsuarioEximioJWT_should_throw_RuntimeException_when_authentication_is_null() {
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(RuntimeException.class,
                () -> helperUseCase.carregarUsuarioEximioJWT(),
                "Deve lançar RuntimeException se a Authentication for nula.");
    }

    @Test
    void getRoleUsuarioLogado_should_return_user_role() {

        String username = "testuser";
        setupAuthenticatedContext(username);
        when(userRepository.findByLogin(username)).thenReturn(Optional.of(mockUser));

        when(mockUser.getRole()).thenReturn(RoleEnum.ADMIN);

        RoleEnum role = helperUseCase.getRoleUsuarioLogado();

        assertEquals(RoleEnum.ADMIN, role, "Deve retornar o RoleEnum do usuário carregado.");
        verify(mockUser).getRole();
    }
}