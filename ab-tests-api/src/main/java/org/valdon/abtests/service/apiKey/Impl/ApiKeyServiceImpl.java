package org.valdon.abtests.service.apiKey.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.valdon.abtests.controller.apiKey.params.ApiKeySearchCriteria;
import org.valdon.abtests.domain.api.ApiKey;
import org.valdon.abtests.domain.api.enums.ApiKeyStatus;
import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.domain.user.User;
import org.valdon.abtests.dto.apiKey.ApiKeyResponse;
import org.valdon.abtests.dto.apiKey.CreateApiKeyRequest;
import org.valdon.abtests.dto.apiKey.CreateApiKeyResponse;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.repository.apiKey.ApiKeyRepository;
import org.valdon.abtests.service.apiKey.ApiKeyHashUtil;
import org.valdon.abtests.service.apiKey.ApiKeyService;
import org.valdon.abtests.service.project.ProjectService;
import org.valdon.abtests.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ProjectService projectService;
    private final UserService userService;

    @Override
    @Transactional
    public CreateApiKeyResponse create(Long userId, Long projectId, CreateApiKeyRequest request) {
        Project project = projectService.getOwnedProject(userId, projectId);
        User user = userService.getUserEntity(userId);

        String rawKey = UUID.randomUUID().toString();
        String hashKey = ApiKeyHashUtil.sha256(rawKey);
        String keyPrefix = rawKey.substring(0, 8);

        ApiKey apiKey = new ApiKey(
                null,
                request.name(),
                keyPrefix,
                hashKey,
                project,
                user,
                ApiKeyStatus.ACTIVE,
                LocalDateTime.now()
        );

        ApiKey savedKey = apiKeyRepository.save(apiKey);

        return new CreateApiKeyResponse(
                savedKey.getId(),
                savedKey.getName(),
                savedKey.getStatus(),
                rawKey
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getAll(Long userId, Long projectId, ApiKeySearchCriteria criteria) {
        projectService.validateProjectOwnership(userId, projectId);
        return apiKeyRepository.findByFilter(projectId, criteria);
    }

    @Override
    @Transactional
    public ApiKeyResponse revoke(Long userId, Long projectId, Long apiKeyId){
        ApiKey apiKey = apiKeyRepository.findApiKey(apiKeyId, userId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("api key does not exists"));
        apiKey.revoke();
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                apiKey.getStatus(),
                apiKey.getCreatedAt()
        );
    }

}
