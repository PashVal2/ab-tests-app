package org.valdon.abtests.dto.user;

import java.io.Serializable;

public record UserResponse(

    Long id,
    String name,
    String username

) implements Serializable { }
