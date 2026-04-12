package org.valdon.abtests.repository.apiKey;

import org.valdon.abtests.controller.apiKey.params.ApiKeySearchCriteria;
import org.valdon.abtests.dto.apiKey.ApiKeyResponse;

import java.util.List;

public interface ApiKeyCustomRepository {

    List<ApiKeyResponse> findByFilter(Long projectId, ApiKeySearchCriteria criteria);

}
