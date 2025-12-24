package com.qwarty.auth.model;

import com.qwarty.auth.lov.UserStatus;
import com.qwarty.core.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
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
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.UNVERIFIED;

    @CreationTimestamp
    @NotNull
    private Instant createdAt;

    @UpdateTimestamp
    @NotNull
    private Instant updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }
}
