package org.valdon.abtests.ex.body;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class ExceptionBody {

    private String code;
    private String message;
    private Map<String, String> errors;

    public ExceptionBody(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
