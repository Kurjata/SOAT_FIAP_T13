package soat_fiap.siaes.interfaces.serviceLabor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceLaborRequest(

        @NotBlank(message = "A descrição da mão de obra é obrigatória.")
        @Size(min = 3, max = 100, message = "A descrição deve ter entre 3 e 100 caracteres.")
        String description,

        @NotNull(message = "O custo da mão de obra é obrigatório.")
        @DecimalMin(value = "0.0", inclusive = false, message = "O custo da mão de obra deve ser maior que zero.")
        @Digits(integer = 10, fraction = 2, message = "O custo da mão de obra deve ter no máximo 10 dígitos inteiros e 2 decimais.")
        BigDecimal laborCost
) {
}
