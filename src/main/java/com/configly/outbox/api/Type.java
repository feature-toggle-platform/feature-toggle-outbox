package com.configly.outbox.api;

public record Type(
        String value
) {

    public static Type create(String type) {
        return new Type(type);
    }

}
