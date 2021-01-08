package org.autex.exception;

public class CalloutException extends RuntimeException {
    public CalloutException(int status, String reason) {
        super(status + " " + reason);
    }
}
