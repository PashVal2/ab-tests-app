package org.valdon.abtests.dto.userEvent;


public record EventSummaryResponse(

        long total,
        long impressions,
        long clicks,
        long views,
        long watchStarts,
        long watchFinishes,
        long likes

) { }
