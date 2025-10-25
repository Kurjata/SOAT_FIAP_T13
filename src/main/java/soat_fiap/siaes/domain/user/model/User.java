package soat_fiap.siaes.domain.user.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import soat_fiap.siaes.domain.user.model.document.Document;
import soat_fiap.siaes.domain.user.model.document.DocumentConverter;
import soat_fiap.siaes.domain.user.model.document.DocumentFactory;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum role;

    @Column(nullable = false, unique = true)
    @Convert(converter = DocumentConverter.class)
    private Document document;

    @Deprecated
    public User() {}

    public User(String name, String login, String password, RoleEnum role, String document, String email) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;
        this.document = DocumentFactory.fromString(document);
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasRole(RoleEnum role) {
        return this.role == role;
    }

    public String getDocumentAsString() {
        return document.getDocumentFormatted();
    }

    public String getIdAsString() {
        return id.toString();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public RoleEnum getRole() {
        return role;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }
}
