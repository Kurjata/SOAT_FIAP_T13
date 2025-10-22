package soat_fiap.siaes.domain.user.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import soat_fiap.siaes.domain.user.model.RoleEnum;
import soat_fiap.siaes.domain.user.model.User;
import soat_fiap.siaes.domain.user.model.document.DocumentFactory;
import soat_fiap.siaes.domain.user.repository.UserRepository;
import soat_fiap.siaes.domain.user.model.document.Document;
import soat_fiap.siaes.interfaces.user.dto.CreateUserRequest;
import soat_fiap.siaes.interfaces.user.dto.UpdateUserRequest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }


    private User createUser(String login, String document) {
        return new User("Vinicius", login, "vinicius123", RoleEnum.CLIENT, document, "vinicius@email.com");
    }

    @Test
    void save__should_return_saved_user_when_data_is_correct() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "123456789", "384.088.920-02", "vinelouzada@gmail.com");
        User user = createUser(request.login(), request.document());

        when(userRepository.existsByLoginOrDocument(eq(request.login()), any(Document.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(request);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo(request.login());
        assertThat(savedUser.getDocumentAsString()).isEqualTo(request.document());
        verify(userRepository).existsByLoginOrDocument(eq(request.login()), any(Document.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void save__should_throw_exception_when_login_or_document_exists() {
        CreateUserRequest request = new CreateUserRequest("Vinicius", "vinelouzada", "123456789", "384.088.920-02", "vinelouzada@gmail.com");

        when(userRepository.existsByLoginOrDocument(eq(request.login()), any(Document.class))).thenReturn(true);

        assertThatThrownBy(() -> userService.save(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void findByDocument__should_return_user_when_exists() {
        String document = "384.088.920-02";
        User user = createUser("login1", document);

        when(userRepository.findByDocument(any(Document.class)))
                .thenReturn(Optional.of(user));

        User found = userService.findByDocument(document);

        assertThat(found).isNotNull();
        assertThat(found.getDocumentAsString()).isEqualTo(document);
        verify(userRepository).findByDocument(any(Document.class));
    }

    @Test
    void findByDocument__should_throw_exception_when_not_found() {
        String document = "384.088.920-02";
        when(userRepository.findByDocument(any(Document.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByDocument(document))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Usuário com documento " + document + " não encontrado");
    }

    @Test
    void update__should_update_user_when_data_is_correct() {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Vinicius Updated", "384.088.920-02", RoleEnum.CLIENT);
        User existingUser = createUser("vinelouzada", request.document());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByDocument(any(Document.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updated = userService.update(userId, request);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo(request.name());
        assertThat(updated.getDocumentAsString()).isEqualTo(request.document());
        assertThat(updated.getRole()).isEqualTo(request.role());
        verify(userRepository).findById(userId);
        verify(userRepository).existsByDocument(any(Document.class));
        verify(userRepository).save(existingUser);
    }

    @Test
    void update__should_throw_exception_when_user_not_found() {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Vinicius Updated", "384.088.920-02", RoleEnum.CLIENT);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Usuário não encontrado");

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update__should_throw_exception_when_document_exists() {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("Vinicius Updated", "384.088.920-02", RoleEnum.CLIENT);
        User existingUser = createUser("vinelouzada", "384.088.920-02");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByDocument(any(Document.class))).thenReturn(true);

        assertThatThrownBy(() -> userService.update(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Documento já cadastrado");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByDocument(any(Document.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteById__should_delete_when_user_exists() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteById(userId);

        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteById__should_throw_exception_when_user_not_found() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Usuário não encontrado");

        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }
}
