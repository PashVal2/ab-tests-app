package org.valdon.abtests.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String credentials;

    // Неаутентифицированный токен
    public ApiKeyAuthenticationToken(String rawApiKey) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = null;
        this.credentials = rawApiKey;
        setAuthenticated(false);
    }

    // Аутентифицированный токен
    public ApiKeyAuthenticationToken(IntegrationPrincipal principal) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = principal;
        this.credentials = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}