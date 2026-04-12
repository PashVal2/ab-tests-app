package org.valdon.abtests.service.apiKey;

import org.valdon.abtests.controller.apiKey.params.ApiKeySearchCriteria;
import org.valdon.abtests.dto.apiKey.ApiKeyResponse;
import org.valdon.abtests.dto.apiKey.CreateApiKeyRequest;
import org.valdon.abtests.dto.apiKey.CreateApiKeyResponse;

import java.util.List;

public interface ApiKeyService {

    CreateApiKeyResponse create(Long userId, Long projectId, CreateApiKeyRequest request);

    List<ApiKeyResponse> getAll(Long userId, Long projectId, ApiKeySearchCriteria criteria);

    ApiKeyResponse revoke(Long userId, Long projectId, Long apiKeyId);

}