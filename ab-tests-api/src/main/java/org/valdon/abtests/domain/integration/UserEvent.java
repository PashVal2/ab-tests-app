package org.valdon.abtests.domain.integration;

import jakarta.persistence.*;
import lombok.*;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;
import org.valdon.abtests.domain.integration.enums.EventType;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_events")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @Column(name = "external_user_id", nullable = false)
    private String externalUserId;

    // id фильма, трека, ролика
    @Column(name = "content_id")
    private String contentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
