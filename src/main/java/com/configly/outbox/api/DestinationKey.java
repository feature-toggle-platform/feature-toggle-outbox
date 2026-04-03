package com.configly.outbox.api;

import java.util.UUID;

public record DestinationKey(
        String value
) {

    public static DestinationKey from(String value) {
        return new DestinationKey(value);
    }

    public static DestinationKey generate(){
        return new DestinationKey(UUID.randomUUID().toString());
    }
}
