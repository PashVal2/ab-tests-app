package org.valdon.abtests.ex;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionBody {

    private String message;
    private Map<String, String> errors;

    public ExceptionBody(String message) {
        this.message = message;
    }
}
