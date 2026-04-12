package org.valdon.abtests.domain.experiment.enums;

import lombok.Getter;
import org.valdon.abtests.domain.integration.enums.EventType;

@Getter
public enum MetricType {

    CTR(EventType.CLICK, EventType.IMPRESSION),
    VIEW_RATE(EventType.VIEW, EventType.IMPRESSION),
    LIKE_RATE(EventType.LIKE, EventType.IMPRESSION),
    WATCH_START_RATE(EventType.WATCH_START, EventType.IMPRESSION),
    WATCH_FINISH_RATE(EventType.WATCH_FINISH, EventType.IMPRESSION),
    WATCH_THROUGH_RATE(EventType.WATCH_FINISH, EventType.WATCH_START);

    private final EventType numerator;
    private final EventType denominator;

    MetricType(EventType numerator, EventType denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

}