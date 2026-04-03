package com.configly.outbox.api;

public class OutboxException extends RuntimeException {
    public OutboxException(String message) {
        super(message);
    }

    public OutboxException(String message, Throwable cause) {
        super(message, cause);
    }
}
