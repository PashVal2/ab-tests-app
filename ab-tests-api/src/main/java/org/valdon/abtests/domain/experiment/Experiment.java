package org.valdon.abtests.domain.experiment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.valdon.abtests.domain.experiment.enums.ExperimentStatus;
import org.valdon.abtests.domain.experiment.enums.MetricType;
import org.valdon.abtests.domain.project.Project;
import org.valdon.abtests.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "experiments")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Experiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experiment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "external_key", nullable = false, length = 100)
    private String externalKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExperimentStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private String nullHypothesis; // H0

    @Column(nullable = false)
    private String alternativeHypothesis; // H1

    // Основная метрика
    @Enumerated(EnumType.STRING)
    @Column(name = "primary_metric", nullable = false, length = 100)
    private MetricType primaryMetric;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

}
