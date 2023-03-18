package com.github.ioridazo.selenium.exception;

public class SeleniumRuntimeException extends RuntimeException {

    private final String body;

    public SeleniumRuntimeException(final Throwable cause, final String body) {
        super(cause);
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
