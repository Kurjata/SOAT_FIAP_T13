package soat_fiap.siaes.domain.user.model.document;

import br.com.caelum.stella.format.CNPJFormatter;
import br.com.caelum.stella.validation.CNPJValidator;

public class CNPJ extends Document{
    public CNPJ(String cnpj) {
        super(cnpj);
    }
    @Override
    public DocumentType getType() {
        return DocumentType.CNPJ;
    }

    @Override
    public boolean isValid() {
        CNPJValidator validator = new CNPJValidator(false);
        try {
            validator.assertValid(this.value);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getDocumentFormatted() {
        CNPJFormatter formatter = new CNPJFormatter();
        return formatter.format(this.value);
    }
}