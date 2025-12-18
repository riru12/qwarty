package com.qwarty.auth.model;

import com.qwarty.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel implements UserDetails {
    @Id
    @NotNull
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Builder.Default
    @NotNull
    @Column(nullable = false)
    private boolean disabled = false;

    @Builder.Default
    @NotNull
    @Column(nullable = false)
    private boolean verified = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }
}
