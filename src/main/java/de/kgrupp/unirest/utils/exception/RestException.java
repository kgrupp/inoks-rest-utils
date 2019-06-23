package de.kgrupp.unirest.utils.exception;

public class RestException extends RuntimeException {
    public RestException(String message) {
        super(message);
    }

    public RestException(Exception exception) {
        super(exception);
    }
}
