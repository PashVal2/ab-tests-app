package org.valdon.abtests.dto.userEvent;

import java.util.List;

public record UserEventPageResponse(

        List<UserEventResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last

) { }
