package br.com.maravilhasnoticias.backend.push;

import br.com.maravilhasnoticias.backend.news.News;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class WebPushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebPushService.class);
    private final PushSubscriptionRepository repository;
    private final String publicKey;
    private final String privateKey;
    private final String subject;

    public WebPushService(PushSubscriptionRepository repository,
                          @Value("${VAPID_PUBLIC_KEY:}") String publicKey,
                          @Value("${VAPID_PRIVATE_KEY:}") String privateKey,
                          @Value("${VAPID_SUBJECT:}") String subject) {
        this.repository = repository; this.publicKey = publicKey; this.privateKey = privateKey; this.subject = subject;
        if (publicKey.isBlank() || privateKey.isBlank() || subject.isBlank()) LOGGER.warn("Web Push não configurado; notificações serão ignoradas");
    }

    @Async
    @Transactional
    public void notifyUrgentNews(News news) {
        if (publicKey.isBlank() || privateKey.isBlank() || subject.isBlank()) return;
        String payload = "{\"title\":\"Notícia urgente\",\"body\":\"" + escape(news.getTitle()) + "\",\"url\":\"/news/" + escape(news.getSlug()) + "\"}";
        try {
            PushService pushService = new PushService(publicKey, privateKey, subject);
            for (PushSubscription subscription : repository.findAllByActiveTrue()) send(pushService, subscription, payload);
        } catch (Exception exception) {
            LOGGER.error("Não foi possível inicializar o envio Web Push: {}", exception.getClass().getSimpleName());
        }
    }

    private void send(PushService pushService, PushSubscription subscription, String payload) {
        try {
            var response = pushService.send(new Notification(subscription.getEndpoint(), subscription.getP256dh(), subscription.getAuth(), payload.getBytes(StandardCharsets.UTF_8)));
            int status = response.getStatusLine().getStatusCode();
            if (status == 404 || status == 410) subscription.deactivate();
            else if (status >= 400) LOGGER.warn("Falha temporária ao enviar Web Push; status={}", status);
        } catch (Exception exception) {
            LOGGER.warn("Falha isolada ao enviar Web Push: {}", exception.getClass().getSimpleName());
        }
    }
    private String escape(String value) { return value.replace("\\", "\\\\").replace("\"", "\\\""); }
}
