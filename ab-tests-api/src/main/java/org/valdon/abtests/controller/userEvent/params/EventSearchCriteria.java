package org.valdon.abtests.controller.userEvent.params;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.valdon.abtests.domain.integration.enums.EventType;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventSearchCriteria {

    private String externalKey;
    private EventType eventType;
    private String externalUserId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdTo;

    private Integer page = 0;
    private Integer size = 50;

}