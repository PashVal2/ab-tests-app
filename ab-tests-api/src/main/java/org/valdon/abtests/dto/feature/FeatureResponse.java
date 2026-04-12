package org.valdon.abtests.dto.feature;

import java.io.Serializable;

public record FeatureResponse(

        Long id,
        String code,
        String name,
        boolean active

) implements Serializable { }
