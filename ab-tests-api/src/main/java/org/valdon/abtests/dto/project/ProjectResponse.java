package org.valdon.abtests.dto.project;

import java.io.Serializable;

public record ProjectResponse(

        Long id,
        String name,
        String code

) implements Serializable { }
