package org.valdon.abtests.dto.apiKey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateApiKeyRequest(

        @NotBlank String name

) { }
