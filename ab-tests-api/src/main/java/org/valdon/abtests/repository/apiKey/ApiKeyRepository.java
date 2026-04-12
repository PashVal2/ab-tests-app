package org.valdon.abtests.repository.apiKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.valdon.abtests.domain.api.ApiKey;
import org.valdon.abtests.domain.api.enums.ApiKeyStatus;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long>, ApiKeyCustomRepository {

    Optional<ApiKey> findByKeyHashAndStatus(String keyHash, ApiKeyStatus status);

    @Query("""
        SELECT k
        FROM ApiKey k
        WHERE k.id = :apiKeyId
            AND k.project.id = :projectId
            AND k.createdBy.id = :userId
    """)
    Optional<ApiKey> findApiKey(Long apiKeyId, Long userId, Long projectId);

}