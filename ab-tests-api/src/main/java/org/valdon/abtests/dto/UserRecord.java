package org.valdon.abtests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.valdon.abtests.validation.OnCreate;
import org.valdon.abtests.validation.OnUpdate;

import java.io.Serializable;

public record UserRecord (

    @Null(groups = { OnCreate.class })
    @NotNull(groups = { OnUpdate.class })
    Long id,

    @NotNull(groups = { OnCreate.class })
    String username,

    @Email
    @NotNull(groups = { OnCreate.class })
    String email,

    @NotNull(groups = { OnCreate.class })
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String pass,

    @NotNull(groups = { OnCreate.class })
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String passConfirm

) implements Serializable { }
