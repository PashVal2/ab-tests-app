package org.valdon.abtests.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.valdon.abtests.ex.*;
import org.valdon.abtests.ex.body.ExceptionBody;

@RestControllerAdvice
public class ControllerAdvise {

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionBody unauthorizedExceptionHandler(UnauthorizedException ex) {
        return new ExceptionBody("UNAUTHORIZED", "Unauthorized");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody methodArgumentNotValidHandler(MethodArgumentNotValidException ex) {
        return new ExceptionBody("INVALID_ARGUMENTS", "Invalid arguments");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody validationHandler(ValidationException ex) {
        return new ExceptionBody("VALIDATION_ERROR", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody IllegalArgumentHandler(ValidationException ex) {
        return new ExceptionBody("ILLEGAL_ARGUMENT", ex.getMessage());
    }

    @ExceptionHandler(EmailNotConfirmedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody emailNotConfirmedHandler(EmailNotConfirmedException ex) {
        return new ExceptionBody("EMAIL_NOT_CONFIRMED", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionBody userAlreadyVerifiedHandler(UserAlreadyVerifiedException ex) {
        return new ExceptionBody("USER_ALREADY_VERIFIED", ex.getMessage());
    }

    @ExceptionHandler(EmailResendRateLimitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody emailResendRateLimitHandler(EmailResendRateLimitException ex) {
        return new ExceptionBody("EMAIL_RESEND_RATE_LIMIT", ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionBody resourceNotFoundHandler(ResourceNotFoundException ex) {
        return new ExceptionBody("RESOURCE_NOT_FOUND", "Resource not found");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionBody userAlreadyExistsHandler(UserAlreadyExistsException ex) {
        return new ExceptionBody("USER_ALREADY_EXISTS", "user already exists");
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody internalServerErrorHandler() {
        return new ExceptionBody("INTERNAL_ERROR", "Internal server error");
    }

}
