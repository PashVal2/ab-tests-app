package org.valdon.abtests.pubsub;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.user.UserCreatedEvent;
import org.valdon.abtests.service.mail.MailService;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final MailService mailService;

    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            ObjectMapper mapper = new ObjectMapper();
            UserCreatedEvent event = mapper.readValue(json, UserCreatedEvent.class);
            mailService.sendRegistrationMail(event);
            log.info("Mail sent for {}", event.username());
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }

}
