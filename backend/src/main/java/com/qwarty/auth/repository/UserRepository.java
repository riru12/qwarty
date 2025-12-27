package com.qwarty.auth.repository;

import com.qwarty.auth.lov.UserStatus;
import com.qwarty.auth.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByIdAndStatusNot(UUID id, UserStatus status);

    Optional<User> findByUsernameAndStatusNot(String username, UserStatus status);

    boolean existsByUsernameAndStatusNot(String username, UserStatus status);

    boolean existsByEmailAndStatusNot(String email, UserStatus status);
}
