package soat_fiap.siaes.interfaces.user.document;

import br.com.caelum.stella.format.CPFFormatter;
import br.com.caelum.stella.validation.CPFValidator;

public class CPF extends Document {
    public CPF(String cpf) {
        super(cpf);
    }

    @Override
    public DocumentType getType() {
        return DocumentType.CPF;
    }

    @Override
    public boolean isValid() {
        CPFValidator validator = new CPFValidator(false);
        try {
            validator.assertValid(this.value);
            return true;
        } catch (Exception e) {
            return false;
       }
    }

    @Override
    public String getDocumentFormatted() {
        CPFFormatter formatter = new CPFFormatter();
        return formatter.format(this.value);
    }
}