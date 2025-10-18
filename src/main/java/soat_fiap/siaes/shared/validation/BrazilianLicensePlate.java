package soat_fiap.siaes.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import soat_fiap.siaes.shared.constraint.BrazilianLicensePlateValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BrazilianLicensePlateValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface BrazilianLicensePlate {

    String message() default "Placa inválida. Use o formato AAA-1234 ou AAA1A23.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
