package soat_fiap.siaes.interfaces.partStock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.NotBlank;
import soat_fiap.siaes.domain.partStock.model.Supply;
import soat_fiap.siaes.domain.partStock.model.UnitMeasure;

import java.math.BigDecimal;

public record CreateSupplyRequest(

        @NotBlank
        String name,

        @NotBlank
        String supplier,

        @NotNull
        @Positive
        BigDecimal unitPrice,

        @NotNull
        UnitMeasure unitMeasure,

        @NotNull
        Boolean available

        ) {

        public Supply toModel() {
                return new Supply(name, unitPrice, unitMeasure, supplier, available);
        }
}
