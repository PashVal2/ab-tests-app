package org.valdon.abtests.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.valdon.abtests.validation.OnCreate;

import java.io.Serializable;

public record RegisterRequest(

        @NotBlank
        @Size(max = 50)
        @NotNull(groups = { OnCreate.class })
        String name,

        @NotBlank
        @Email
        @Size(max = 100)
        @NotNull(groups = { OnCreate.class })
        String username,

        @NotBlank
        @Size(min = 8, max = 71)
        @NotNull(groups = { OnCreate.class })
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,

        @NotBlank
        @Size(min = 8, max = 71)
        @NotNull(groups = { OnCreate.class })
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String passwordConfirm

) implements Serializable { }
