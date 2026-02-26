package org.valdon.abtests.dto.jwt;

import java.util.Set;

public record JwtUserRecord(

        Long id,
        Set<String> roles

) { }
