package org.valdon.abtests.domain.api;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.valdon.abtests.domain.api.enums.ApiKeyStatus;
import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 16)
    private String keyPrefix;

    @Column(nullable = false, unique = true, length = 64)
    private String keyHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiKeyStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public void revoke() {
        this.status = ApiKeyStatus.REVOKED;
    }

}
