package pl.feature.toggle.service;

import pl.feature.toggle.service.api.OutboxException;
import org.apache.commons.lang3.StringUtils;

record Attempts(
        int attempt,
        int limit,
        String lastErrorMessage
) {

    Attempts {
        if (limit <= 0) {
            throw new OutboxException("Attempt limit must be greater than 0");
        }
        if (attempt < 0) {
            throw new OutboxException("Attempt cannot be smaller than 0");
        }
    }

    boolean limitReached() {
        return attempt >= limit;
    }

    Attempts increase(final String errorMessage) {
        if (attempt < limit) {
            return new Attempts(attempt + 1, limit, errorMessage);
        }
        throw new OutboxException("Attempt limit exceeded");
    }

    static Attempts zero(final int limit) {
        return new Attempts(0, limit, StringUtils.EMPTY);
    }
}
