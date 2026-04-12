package org.valdon.abtests.domain.integration;

import jakarta.persistence.*;
import lombok.*;
import org.valdon.abtests.domain.experiment.Experiment;
import org.valdon.abtests.domain.experiment.Variant;

@Entity
@Table(name = "metric_results")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetricResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "sample_size", nullable = false)
    private Long assignedUsers;

    @Column(name = "denominator_users", nullable = false)
    private Long denominatorUsers;

    @Column(name = "numerator_users", nullable = false)
    private Long numeratorUsers;

    @Column(name = "impressions", nullable = false)
    private Long impressions;

    @Column(name = "clicks", nullable = false)
    private Long clicks;

    @Column(name = "views", nullable = false)
    private Long views;

    @Column(name = "watch_starts", nullable = false)
    private Long watchStarts;

    @Column(name = "watch_finishes", nullable = false)
    private Long watchFinishes;

    @Column(name = "likes", nullable = false)
    private Long likes;

    @Column(name = "uplift_percent")
    private Double upliftPercent;

    @Column(name = "p_value")
    private Double pValue;

    @Column(name = "diff_from_control")
    private Double diffFromControl;

    @Column(name = "ci_95_lower")
    private Double ci95Lower;

    @Column(name = "ci_95_upper")
    private Double ci95Upper;

    @Column(name = "statistically_significant", nullable = false)
    private boolean statisticallySignificant;

}
