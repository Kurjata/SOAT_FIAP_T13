package soat_fiap.siaes.interfaces.user.document;

import lombok.Getter;
import org.springframework.util.Assert;

@Getter
public abstract class Document {
    protected final String value;

    public Document(String value) {
        Assert.notNull(value, "value must not be null");
        Assert.hasText(value, "value must not be empty");
        this.value = sanitizeDocument(value);

        Assert.isTrue(this.isValid(), "Document is invalid");
    }

    protected String sanitizeDocument(String document) {
        return document.replaceAll("[^0-9]", "").trim();
    }

    public abstract DocumentType getType();
    public abstract boolean isValid();
    public abstract String getDocumentFormatted();
}