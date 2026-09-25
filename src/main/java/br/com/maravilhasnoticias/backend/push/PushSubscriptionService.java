package br.com.maravilhasnoticias.backend.push;

import br.com.maravilhasnoticias.backend.push.dto.PushSubscriptionRequest;
import br.com.maravilhasnoticias.backend.push.dto.PushSubscriptionResponse;
import br.com.maravilhasnoticias.backend.user.User;
import br.com.maravilhasnoticias.backend.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PushSubscriptionService {
    private final PushSubscriptionRepository repository;
    private final UserRepository userRepository;
    public PushSubscriptionService(PushSubscriptionRepository repository, UserRepository userRepository) {
        this.repository = repository; this.userRepository = userRepository;
    }
    @Transactional
    public PushSubscriptionResponse subscribe(PushSubscriptionRequest request, Authentication authentication) {
        User user = authenticatedUser(authentication);
        PushSubscription subscription = repository.findByEndpoint(request.endpoint()).orElseGet(() ->
                new PushSubscription(request.endpoint(), request.keys().p256dh(), request.keys().auth(), user));
        subscription.update(request.endpoint(), request.keys().p256dh(), request.keys().auth(), user);
        return PushSubscriptionResponse.from(repository.save(subscription));
    }
    @Transactional
    public void unsubscribe(PushSubscriptionRequest request) {
        repository.findByEndpoint(request.endpoint()).ifPresent(PushSubscription::deactivate);
    }
    private User authenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) return null;
        try { return userRepository.findById(UUID.fromString(jwt.getSubject())).orElse(null); }
        catch (IllegalArgumentException exception) { return null; }
    }
}
