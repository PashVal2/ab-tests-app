package org.valdon.abtests.dto.integration;

import jakarta.validation.constraints.NotBlank;

public record ConfigRequest(

        @NotBlank String experimentExternalKey,
        @NotBlank String externalUserId

) { }