package org.valdon.abtests.dto.user;

import java.util.Set;

public record UserWithRolesResponse (

        Long id,
        String name,
        String username,
        Set<String> roles

) { }
