package pl.feature.toggle.service.outbox;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@Getter
@Accessors(fluent = true)
final class ApplicationInfoProvider implements ApplicationListener<ApplicationReadyEvent> {

    private volatile String applicationName = "unknown";


    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        this.applicationName = event.getApplicationContext().getEnvironment().getProperty("spring.application.name");
    }
}
