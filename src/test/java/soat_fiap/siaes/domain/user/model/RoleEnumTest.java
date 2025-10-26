package soat_fiap.siaes.domain.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleEnumTest {
    @Test
    void contains_should_return_true_when_role_exists() {

        String existingRole = "admin";

        assertTrue(RoleEnum.contains(existingRole), "Deve retornar true para o role 'admin'.");
        assertTrue(RoleEnum.contains("client"), "Deve retornar true para o role 'client'.");
    }

    @Test
    void contains_should_return_false_when_role_does_not_exist() {

        String nonExistingRole = "financeiro";

        assertFalse(RoleEnum.contains(nonExistingRole), "Deve retornar false para um role inexistente.");
    }

    @Test
    void enum_values_should_match_expected_strings() {

        assertThat(RoleEnum.ADMIN.getRole()).isEqualTo("admin");
        assertThat(RoleEnum.COLLABORATOR.getRole()).isEqualTo("collaborator");
        assertThat(RoleEnum.CLIENT.getRole()).isEqualTo("client");
    }
}
