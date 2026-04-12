package org.valdon.abtests.dto.integration;

public record AssignUserResponse(

        Long variantId,
        String variantName,
        String externalCode,
        boolean control

) { }