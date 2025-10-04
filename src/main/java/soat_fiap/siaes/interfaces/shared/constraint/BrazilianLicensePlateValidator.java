package soat_fiap.siaes.interfaces.shared.constraint;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import soat_fiap.siaes.interfaces.shared.validation.BrazilianLicensePlate;

/**
 * Validator for Brazilian vehicle license plates.
 * Supports both old format (AAA-1234) and Mercosul format (AAA1A23).
 */
public class BrazilianLicensePlateValidator implements ConstraintValidator<BrazilianLicensePlate, String> {

    // Regex for old format: AAA-1234
    private static final String OLD_FORMAT_REGEX = "^[A-Z]{3}-\\d{4}$";

    // Regex for Mercosul format: AAA1A23
    private static final String MERCOSUL_FORMAT_REGEX = "^[A-Z]{3}\\d[A-Z0-9]\\d{2}$";

    // Regex for patterns that should be rejected (like AAA-0000)
    private static final String ALL_ZERO_PATTERN = "^[A-Z]{3}-?0{4}$";

    // Regex for repeated number sequences (optional)
    private static final String REPEATED_NUMBER_PATTERN = "^[A-Z]{3}-?([0-9])\\1{3}$";

    @Override
    public boolean isValid(String plate, ConstraintValidatorContext context) {
        if (plate == null || plate.isBlank()) {
            return false; // null or empty is not valid
        }

        String normalized = plate.toUpperCase().trim();

        // Check if plate matches either old or Mercosul format
        if (!matchesFormat(normalized)) {
            return false;
        }

        // Reject clearly invalid patterns
        if (matchesInvalidPattern(normalized)) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the plate matches either valid format (old or Mercosul).
     */
    private boolean matchesFormat(String plate) {
        return plate.matches(OLD_FORMAT_REGEX) || plate.matches(MERCOSUL_FORMAT_REGEX);
    }

    /**
     * Checks if the plate matches any obviously invalid patterns.
     */
    private boolean matchesInvalidPattern(String plate) {
        return plate.matches(ALL_ZERO_PATTERN) || plate.matches(REPEATED_NUMBER_PATTERN);
    }
}
