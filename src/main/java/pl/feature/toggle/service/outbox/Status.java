package pl.feature.toggle.service.outbox;

record Status(
        Outbox status
) {

    boolean isNew() {
        return status == Outbox.NEW;
    }

    boolean isPublished() {
        return status == Outbox.PUBLISHED;
    }

    boolean isFailed() {
        return status == Outbox.FAILED;
    }

    boolean isOk() {
        return status == Outbox.OK;
    }

    boolean isProcessing() {
        return status == Outbox.PROCESSING;
    }

    static Status createNew() {
        return new Status(Outbox.NEW);
    }

    static Status createPublished() {
        return new Status(Outbox.PUBLISHED);
    }

    static Status createFailed() {
        return new Status(Outbox.FAILED);
    }

    static Status createOk() {
        return new Status(Outbox.OK);
    }

    static Status createProcessing() {
        return new Status(Outbox.PROCESSING);
    }

    String rawStatus() {
        return status.name();
    }

    enum Outbox {
        NEW,
        PROCESSING,
        PUBLISHED,
        FAILED,
        OK
    }

}
