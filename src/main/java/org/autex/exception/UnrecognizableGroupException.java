package org.autex.exception;

public class UnrecognizableGroupException extends RuntimeException {
    public UnrecognizableGroupException(String code) {
        super("Code " + code + " not recognized");
    }
}
