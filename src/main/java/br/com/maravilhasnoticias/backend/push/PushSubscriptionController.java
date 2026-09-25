package br.com.maravilhasnoticias.backend.push;

import br.com.maravilhasnoticias.backend.push.dto.PushSubscriptionRequest;
import br.com.maravilhasnoticias.backend.push.dto.PushSubscriptionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push/subscriptions")
public class PushSubscriptionController {
    private final PushSubscriptionService service;
    public PushSubscriptionController(PushSubscriptionService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PushSubscriptionResponse subscribe(@Valid @RequestBody PushSubscriptionRequest request, Authentication authentication) { return service.subscribe(request, authentication); }
    @DeleteMapping @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@Valid @RequestBody PushSubscriptionRequest request) { service.unsubscribe(request); }
}
