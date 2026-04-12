package org.valdon.abtests.controller.apiKey.params;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.valdon.abtests.domain.api.enums.ApiKeyStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiKeySearchCriteria {

    private String name;
    private String keyPrefix;
    private ApiKeyStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdTo;

}