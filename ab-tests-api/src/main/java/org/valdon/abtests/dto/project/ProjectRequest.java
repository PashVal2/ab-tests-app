package org.valdon.abtests.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.aspectj.bridge.IMessage;

public record ProjectRequest(

        @NotBlank(message = "project name is required")
        @Size(max = 150, message = "project name must be <= 150 chars")
        String name

) { }