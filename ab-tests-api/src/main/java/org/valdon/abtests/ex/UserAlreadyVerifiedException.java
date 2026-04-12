package org.valdon.abtests.ex;

public class UserAlreadyVerifiedException extends RuntimeException {
    public UserAlreadyVerifiedException(String message){
        super(message);
    }
}
