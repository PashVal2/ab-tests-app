package org.valdon.abtests.ex;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message){
        super(message);
    }
}
