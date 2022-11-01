package com.nimfid.commons.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenException extends Exception {

    private final HttpStatus status;

    public RefreshTokenException() {
        super("Error: Uknown");
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public RefreshTokenException(final HttpStatus status) {
        this.status = status;
    }

    public RefreshTokenException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public RefreshTokenException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public RefreshTokenException(final String message) {
        super(message);
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public RefreshTokenException(final String message, final Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
