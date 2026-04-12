package org.valdon.abtests.controller.apiKey;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.valdon.abtests.controller.apiKey.params.ApiKeySearchCriteria;
import org.valdon.abtests.dto.apiKey.ApiKeyResponse;
import org.valdon.abtests.dto.apiKey.CreateApiKeyRequest;
import org.valdon.abtests.dto.apiKey.CreateApiKeyResponse;
import org.valdon.abtests.security.UserPrincipal;
import org.valdon.abtests.service.apiKey.ApiKeyService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
@Validated
public class ApiKeyController {

    private static final String PROJECT_ID_HEADER = "X-Project-Id";

    private final ApiKeyService apiKeyService;

    @PostMapping
    public CreateApiKeyResponse create(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @Valid @RequestBody CreateApiKeyRequest request
    ) {
        return apiKeyService.create(user.getId(), projectId, request);
    }

    @GetMapping
    public List<ApiKeyResponse> getAll(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @ModelAttribute ApiKeySearchCriteria criteria
    ) {
        return apiKeyService.getAll(user.getId(), projectId, criteria);
    }

    @PostMapping("/{apiKeyId}/revoke")
    public ApiKeyResponse revoke(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestHeader(PROJECT_ID_HEADER) Long projectId,
            @PathVariable Long apiKeyId
    ) {
        return apiKeyService.revoke(user.getId(), projectId, apiKeyId);
    }

}