package org.valdon.abtests.ex;

public class EmailResendRateLimitException extends RuntimeException {
    public EmailResendRateLimitException(String message){
        super(message);
    }
}
