package de.kgrupp.inoksrestutils.exception;

public class RestException extends RuntimeException {
    public RestException(String message) {
        super(message);
    }

    public RestException(Exception exception) {
        super(exception);
    }
}
