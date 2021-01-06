package org.autex.exception;

public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials() {
        super("Hibás jelszó");
    }
}
