package com.nimfid.commons.exception;

import org.springframework.http.HttpStatus;

public class AccessTokenException extends Exception {

    private final HttpStatus status;

    public AccessTokenException() {
        super("Error: Uknown");
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AccessTokenException(final HttpStatus status) {
        this.status = status;
    }

    public AccessTokenException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public AccessTokenException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public AccessTokenException(final String message) {
        super(message);
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AccessTokenException(final String message, final Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
