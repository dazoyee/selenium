package com.github.ioridazo.selenium.exception;

public class MaintenanceException extends RuntimeException {

    private final String body;

    public MaintenanceException(final Throwable cause, final String body) {
        super(cause);
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
