package org.valdon.abtests.ex;

public class EmailNotConfirmedException extends RuntimeException {
    public EmailNotConfirmedException(String message){
        super(message);
    }
}
