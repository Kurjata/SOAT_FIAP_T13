package soat_fiap.siaes.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    ADMIN("admin"),
    COLLABORATOR("collaborator");

    private final String role;

    public static boolean contains(String role) {
        for (RoleEnum r : RoleEnum.values()) {
            if (r.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }
}
