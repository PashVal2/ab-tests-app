package org.valdon.abtests.domain.experiment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "variants")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    private Experiment experiment;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_control", nullable = false)
    private boolean control;

    @Column(name = "traffic_percent", nullable = false)
    private Double trafficPercent;

    @Column(name = "external_code", nullable = false, length = 50)
    private String externalCode;

}
