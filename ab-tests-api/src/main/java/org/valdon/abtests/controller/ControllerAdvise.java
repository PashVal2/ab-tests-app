package org.valdon.abtests.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.valdon.abtests.ex.UsernameAlreadyExistsException;
import org.valdon.abtests.ex.ExceptionBody;
import org.valdon.abtests.ex.ResourceNotFoundException;
import org.valdon.abtests.ex.ValidationException;

@RestControllerAdvice
public class ControllerAdvise {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody methodArgumentNotValidHandler(MethodArgumentNotValidException ex) {
        return new ExceptionBody("invalid arguments");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody resourceNotFoundHandler(ResourceNotFoundException ex) {
        return new ExceptionBody(ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody emailAlreadyExistsHandler(UsernameAlreadyExistsException ex) {
        return new ExceptionBody(ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody validationFoundHandler(ValidationException ex) {
        return new ExceptionBody(ex.getMessage());
    }

}
