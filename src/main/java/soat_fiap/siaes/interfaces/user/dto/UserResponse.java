package soat_fiap.siaes.interfaces.user.dto;

import soat_fiap.siaes.domain.user.model.User;

public record UserResponse(
    String id,
    String name,
    String login,
    String role,
    String document
){
    public UserResponse(User user) {
        this(user.getId().toString(), user.getName(), user.getLogin(), user.getRole().toString(), user.getDocumentAsString());
    }
}