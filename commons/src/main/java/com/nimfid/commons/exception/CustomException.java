package com.nimfid.commons.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends Exception{
    private final HttpStatus status;

    public CustomException() {
        super("Error: Uknown");
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(final HttpStatus status) {
        this.status = status;
    }

    public CustomException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public CustomException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public CustomException(final String message) {
        super(message);
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomException(final String message, final Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
