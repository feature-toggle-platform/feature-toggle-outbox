package com.configly.outbox;

class FakeEvent {

    protected static final String TYPE = FakeEvent.class.getName();

    public static FakeEvent build() {
        return new FakeEvent();
    }

}
