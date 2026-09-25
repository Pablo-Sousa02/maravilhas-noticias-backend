package br.com.maravilhasnoticias.backend.push;

import br.com.maravilhasnoticias.backend.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "push_subscriptions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushSubscription {
    @Id @UuidGenerator private UUID id;
    @Column(nullable = false, unique = true, length = 2000) private String endpoint;
    @Column(nullable = false, length = 500) private String p256dh;
    @Column(nullable = false, length = 500) private String auth;
    @Column(nullable = false) private boolean active;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    @CreationTimestamp @Column(name = "created_at", nullable = false, updatable = false) private OffsetDateTime createdAt;
    @UpdateTimestamp @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;

    public PushSubscription(String endpoint, String p256dh, String auth, User user) { update(endpoint, p256dh, auth, user); }
    public void update(String endpoint, String p256dh, String auth, User user) {
        this.endpoint = endpoint; this.p256dh = p256dh; this.auth = auth; this.user = user; this.active = true;
    }
    public void deactivate() { this.active = false; }
}
