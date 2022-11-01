package com.nimfid.commons.exception;

import org.springframework.http.HttpStatus;

public class GatewayException extends RuntimeException{

    private final HttpStatus status;

    public GatewayException() {
        super("Error: Uknown");
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public GatewayException(final HttpStatus status) {
        this.status = status;
    }

    public GatewayException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public GatewayException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public GatewayException(final String message) {
        super(message);
        status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public GatewayException(final String message, final Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
