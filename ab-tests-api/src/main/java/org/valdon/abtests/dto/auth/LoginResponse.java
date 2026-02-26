package org.valdon.abtests.dto.auth;

import java.io.Serializable;

public record LoginResponse(

        String accessToken

) implements Serializable { }
