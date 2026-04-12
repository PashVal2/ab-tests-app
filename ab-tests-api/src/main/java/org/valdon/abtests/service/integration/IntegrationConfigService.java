package org.valdon.abtests.service.integration;

import org.valdon.abtests.dto.integration.ConfigResponse;

public interface IntegrationConfigService {

    ConfigResponse getConfig(Long projectId, String experimentExternalId, String externalUserId);

}
