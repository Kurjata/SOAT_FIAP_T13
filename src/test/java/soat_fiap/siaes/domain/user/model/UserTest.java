package soat_fiap.siaes.domain.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import soat_fiap.siaes.domain.user.model.document.Document;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserTest {

    private final String VALID_NAME = "Teste Silva";
    private final String VALID_LOGIN = "teste.silva";
    private final String VALID_PASSWORD = "hashedPassword123";
    private final RoleEnum VALID_ROLE = RoleEnum.COLLABORATOR;
    private final String VALID_CPF_DOC = "12345678909";
    private final String VALID_EMAIL = "teste@exemplo.com";

    private final UUID TEST_UUID = UUID.randomUUID();

    private User createValidUser() {
        User user = new User(VALID_NAME, VALID_LOGIN, VALID_PASSWORD, VALID_ROLE, VALID_CPF_DOC, VALID_EMAIL);
        user.setId(TEST_UUID);
        return user;
    }

    @Test
    void constructor_should_initialize_all_fields_and_create_document() {

        User user = new User(VALID_NAME, VALID_LOGIN, VALID_PASSWORD, VALID_ROLE, VALID_CPF_DOC, VALID_EMAIL);

        assertThat(user.getName()).isEqualTo(VALID_NAME);
        assertThat(user.getLogin()).isEqualTo(VALID_LOGIN);
        assertThat(user.getPassword()).isEqualTo(VALID_PASSWORD);
        assertThat(user.getRole()).isEqualTo(VALID_ROLE);
        assertThat(user.getEmail()).isEqualTo(VALID_EMAIL);
        assertThat(user.getDocument()).isInstanceOf(Document.class);
    }

    @Test
    void getAuthorities_should_return_list_with_role() {

        User user = createValidUser();

        GrantedAuthority expectedAuthority = new SimpleGrantedAuthority("ROLE_" + VALID_ROLE.name());

        List<GrantedAuthority> authoritiesList = user.getAuthorities().stream()
                .map(auth -> (GrantedAuthority) auth)
                .collect(java.util.stream.Collectors.toList());

        assertThat(authoritiesList).hasSize(1);

        assertThat(authoritiesList).containsExactly(expectedAuthority);
    }

    @Test
    void getUsername_should_return_login() {
        User user = createValidUser();
        assertThat(user.getUsername()).isEqualTo(VALID_LOGIN);
    }

    @Test
    void security_flags_should_always_return_true() {
        User user = createValidUser();
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void hasRole_should_return_true_when_role_matches() {
        User user = createValidUser();
        assertTrue(user.hasRole(RoleEnum.COLLABORATOR));
    }

    @Test
    void hasRole_should_return_false_when_role_does_not_match() {
        User user = createValidUser();
        assertFalse(user.hasRole(RoleEnum.ADMIN));
    }

    @Test
    void getDocumentAsString_should_return_formatted_document() {
        User user = createValidUser();
        assertThat(user.getDocumentAsString()).isNotBlank();
    }

    @Test
    void getIdAsString_should_return_string_representation_of_id() {
        User user = createValidUser();
        assertThat(user.getIdAsString()).isEqualTo(TEST_UUID.toString());
    }

    @Test
    void noArgsConstructor_should_create_user_object() {
        User user = new User();
        assertThat(user).isNotNull();
        assertThat(user.getName()).isNull();
    }
}
