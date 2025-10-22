package soat_fiap.siaes.domain.user.model.document;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DocumentConverter implements AttributeConverter<Document, String> {

    @Override
    public String convertToDatabaseColumn(Document document) {
        return document != null ? document.getValue() : null;
    }

    @Override
    public Document convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        if (dbData.length() == 11) return new CPF(dbData);
        else if (dbData.length() == 14) return new CNPJ(dbData);
        else throw new IllegalArgumentException("Documento inválido: " + dbData);
    }
}