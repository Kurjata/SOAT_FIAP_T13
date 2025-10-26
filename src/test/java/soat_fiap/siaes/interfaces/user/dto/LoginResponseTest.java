package soat_fiap.siaes.interfaces.user.dto;

import org.junit.jupiter.api.Test;
import soat_fiap.siaes.domain.user.model.RoleEnum;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginResponseTest {
    private final String ACCESS_TOKEN = "jwt-access-token-12345";
    private final String REFRESH_TOKEN = "jwt-refresh-token-67890";
    private final String USERNAME = "teste.usuario";
    private final RoleEnum ROLE = RoleEnum.ADMIN;

    @Test
    void should_create_record_and_access_fields_correctly() {

        LoginResponse response = new LoginResponse(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                USERNAME,
                ROLE
        );

        assertThat(response.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.refreshToken()).isEqualTo(REFRESH_TOKEN);
        assertThat(response.username()).isEqualTo(USERNAME);
        assertThat(response.roleEnum()).isEqualTo(ROLE);
    }

    @Test
    void should_return_correct_string_representation() {

        LoginResponse response = new LoginResponse(
                ACCESS_TOKEN,
                REFRESH_TOKEN,
                USERNAME,
                ROLE
        );


        String toStringResult = response.toString();

        assertThat(toStringResult)
                .contains("accessToken=" + ACCESS_TOKEN)
                .contains("refreshToken=" + REFRESH_TOKEN)
                .contains("username=" + USERNAME)
                .contains("roleEnum=" + ROLE.name());
    }

    @Test
    void should_support_equality_and_hashing() {

        LoginResponse response1 = new LoginResponse(ACCESS_TOKEN, REFRESH_TOKEN, USERNAME, ROLE);
        LoginResponse response2 = new LoginResponse(ACCESS_TOKEN, REFRESH_TOKEN, USERNAME, ROLE);
        LoginResponse differentResponse = new LoginResponse(ACCESS_TOKEN, "different-token", USERNAME, ROLE);


        assertThat(response1).isEqualTo(response2);
        assertThat(response1).isNotEqualTo(differentResponse);


        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());

        assertThat(response1.hashCode()).isNotEqualTo(differentResponse.hashCode());
    }
}
