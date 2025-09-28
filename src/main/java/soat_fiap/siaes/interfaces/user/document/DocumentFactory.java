package soat_fiap.siaes.interfaces.user.document;

import org.springframework.util.Assert;

public class DocumentFactory {
    public static Document fromString(String value) {
        Assert.hasText(value, "Valor não deve ser vazio");
        String numeric = value.replaceAll("\\D", "");

        if (numeric.length() == 11) return new CPF(numeric);
        if (numeric.length() == 14) return new CNPJ(numeric);
        throw new IllegalArgumentException("Documento inválido: " + value);
    }
}