package com.qwarty.auth.model;

import com.qwarty.core.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @GeneratedValue
    private UUID id;

    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String passwordHash;

    @Builder.Default
    @NotNull
    private boolean disabled = false;

    @Builder.Default
    @NotNull
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
