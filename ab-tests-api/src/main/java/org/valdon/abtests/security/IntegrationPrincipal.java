package org.valdon.abtests.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class IntegrationPrincipal {

    private final Long apiKeyId;
    private final Long projectId;

}
