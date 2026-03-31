package pl.feature.toggle.service.outbox.api;


public interface OutboxWriter {

    void write(OutboxEvent event);

}
