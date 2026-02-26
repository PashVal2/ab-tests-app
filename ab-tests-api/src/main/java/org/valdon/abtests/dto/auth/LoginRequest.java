package org.valdon.abtests.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record LoginRequest(

        @NotNull
        @Email
        String username,

        @NotNull
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password

) implements Serializable { }
