package org.valdon.abtests.dto.integration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssignUserRequest (

        @NotNull
        @NotBlank String experimentExternalKey,

        @NotNull
        @NotBlank
        String externalUserId

) { }