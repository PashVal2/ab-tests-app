package org.valdon.abtests.dto.user;

public record UserCreatedEvent(

    Long id,
    String name,
    String username,
    String token

) { }
