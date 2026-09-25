package br.com.maravilhasnoticias.backend.push.dto;

import br.com.maravilhasnoticias.backend.push.PushSubscription;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PushSubscriptionResponse(UUID id, String endpoint, boolean active, OffsetDateTime createdAt) {
    public static PushSubscriptionResponse from(PushSubscription value) {
        return new PushSubscriptionResponse(value.getId(), value.getEndpoint(), value.isActive(), value.getCreatedAt());
    }
}
