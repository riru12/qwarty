package com.qwarty.auth.security;

import com.qwarty.auth.lov.UserType;
import java.util.Collections;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class GuestAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final UserType userType = UserType.GUEST;

    public GuestAuthenticationToken(String username) {
        super(Collections.emptyList());
        this.username = username;
        setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return username;
    }
}
