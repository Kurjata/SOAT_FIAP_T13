package soat_fiap.siaes.domain.user.model.document;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CPFTest {
    @Test
    void should_create_valid_cpf() {
        CPF cpf = new CPF("123.456.789-09");

        assertThat(cpf.getValue()).isEqualTo("12345678909");
        assertThat(cpf.isValid()).isTrue();
        assertThat(cpf.getType()).isEqualTo(DocumentType.CPF);
        assertThat(cpf.getDocumentFormatted()).isEqualTo("123.456.789-09");
    }

    @Test
    void should_throw_exception_for_invalid_cpf() {
        assertThatThrownBy(() -> new CPF("111.111.111-11"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Documento inválido");
    }

    @Test
    void should_throw_exception_for_null_or_empty_cpf() {
        assertThatThrownBy(() -> new CPF(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor não deve ser nulo");

        assertThatThrownBy(() -> new CPF(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor não deve ser vazio");
    }
}