package org.valdon.abtests.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.io.Serializable;

public record LoginRequest(

        @Email
        @Size(max = 100)
        @NotBlank
        String username,


        @Size(max = 71)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank
        String password

) implements Serializable { }
