package soat_fiap.siaes.domain.user.model.document;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CNPJTest {
    @Test
    void should_create_valid_cnpj() {
        CNPJ cnpj = new CNPJ("12.345.678/0001-95");

        assertThat(cnpj.getValue()).isEqualTo("12345678000195");
        assertThat(cnpj.isValid()).isTrue();
        assertThat(cnpj.getType()).isEqualTo(DocumentType.CNPJ);
        assertThat(cnpj.getDocumentFormatted()).isEqualTo("12.345.678/0001-95");
    }

    @Test
    void should_throw_exception_for_invalid_cnpj() {
        assertThatThrownBy(() -> new CNPJ("11.111.111/1111-11"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Documento inválido");
    }

    @Test
    void should_throw_exception_for_null_or_empty_cnpj() {
        assertThatThrownBy(() -> new CNPJ(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor não deve ser nulo");

        assertThatThrownBy(() -> new CNPJ(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor não deve ser vazio");
    }
}