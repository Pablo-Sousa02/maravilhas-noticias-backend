CREATE TABLE push_subscriptions (
    id UUID PRIMARY KEY,
    endpoint VARCHAR(2000) NOT NULL,
    p256dh VARCHAR(500) NOT NULL,
    auth VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    user_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_push_subscriptions_endpoint UNIQUE (endpoint),
    CONSTRAINT fk_push_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_push_subscriptions_active ON push_subscriptions(active);
CREATE INDEX idx_push_subscriptions_user ON push_subscriptions(user_id);
