create table outbox_events
(
    event_id     UUID PRIMARY KEY,
    type         VARCHAR(100) NOT NULL,
    payload_json JSONB        NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    attempts     INT          NOT NULL DEFAULT 0,
    max_attempts INT          NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    occurred_at  TIMESTAMPTZ  NOT NULL,
    error_msg    TEXT,
    producer     VARCHAR(100) NOT NULL,
    topic        VARCHAR(100) NOT NULL
)