package org.valdon.abtests.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.valdon.abtests.security.UserPrincipal;

import java.util.Objects;

@Component("cse")
@RequiredArgsConstructor
public class CustomSecurityExpression {

    public boolean canAccess(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        return Objects.equals(id, user.getId());
    }

}
