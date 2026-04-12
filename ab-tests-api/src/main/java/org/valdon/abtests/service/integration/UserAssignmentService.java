package org.valdon.abtests.service.integration;

import org.valdon.abtests.domain.integration.UserAssignment;
import org.valdon.abtests.dto.integration.AssignUserResponse;

import java.util.Map;

public interface UserAssignmentService {

    AssignUserResponse assign(Long projectId, String experimentExternalKey, String externalUserId);

    UserAssignment getUserAssignmentEntity(Long experimentId, String externalUserId);

    Map<Long, Long> countByVariant(Long experimentId);

}