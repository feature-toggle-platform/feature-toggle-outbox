package pl.feature.toggle.service;

import com.ftaas.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.api.OutboxRepository;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;

import static pl.feature.ftaas.outbox.jooq.tables.OutboxEvents.OUTBOX_EVENTS;
import static org.jooq.impl.DSL.*;

@AllArgsConstructor
class OutboxJooqRepository implements OutboxRepository {

    private final DSLContext dslContext;


    @Override
    public <T extends IntegrationEvent> List<Outbox<T>> findUnprocessedOutboxes(final int limit) {
        return dslContext.transactionResult(cfg -> {
            var ctx = DSL.using(cfg);

            var cteName = "picked";
            var pickedId = field(name(cteName, OUTBOX_EVENTS.EVENT_ID.getName()), OUTBOX_EVENTS.EVENT_ID.getDataType());

            var updated = ctx
                    .with(cteName).as(
                            select(OUTBOX_EVENTS.EVENT_ID)
                                    .from(OUTBOX_EVENTS)
                                    .where(OUTBOX_EVENTS.STATUS.eq(Status.Outbox.NEW.name()))
                                    .orderBy(OUTBOX_EVENTS.CREATED_AT.asc())
                                    .limit(limit)
                                    .forUpdate()
                                    .skipLocked()
                    )
                    .update(OUTBOX_EVENTS)
                    .set(OUTBOX_EVENTS.STATUS, Status.Outbox.PROCESSING.name())
                    .where(OUTBOX_EVENTS.EVENT_ID.in(
                            select(pickedId).from(name(cteName))
                    ))
                    .returning()
                    .fetch();

            return updated.map(OutboxMapper::toDomain);
        });
    }

    @Override
    public <T extends IntegrationEvent> void update(final Outbox<T> outbox) {
        dslContext.update(OUTBOX_EVENTS)
                .set(OUTBOX_EVENTS.STATUS, outbox.status().rawStatus())
                .set(OUTBOX_EVENTS.ATTEMPTS, outbox.attempts().attempt())
                .set(OUTBOX_EVENTS.ERROR_MSG, outbox.attempts().lastErrorMessage())
                .where(OUTBOX_EVENTS.EVENT_ID.in(outbox.eventId().id()))
                .execute();
    }

    @Override
    public <T extends IntegrationEvent> void save(final Outbox<T> outbox) {
        dslContext.insertInto(OUTBOX_EVENTS)
                .set(OutboxMapper.fromDomain(outbox))
                .execute();
    }
}
