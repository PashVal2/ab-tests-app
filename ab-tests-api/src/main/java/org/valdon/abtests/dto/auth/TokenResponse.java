package org.valdon.abtests.dto.auth;

import java.io.Serializable;

public record TokenResponse(

        String access

) implements Serializable { }
