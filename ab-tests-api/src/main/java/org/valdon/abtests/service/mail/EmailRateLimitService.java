package org.valdon.abtests.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.valdon.abtests.ex.EmailResendRateLimitException;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailRateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final Duration RESEND_TTL = Duration.ofSeconds(60);
    private static final String RESEND_PREFIX = "mail-resend";

    public void checkResend(String email) {
        String key = RESEND_PREFIX + email.trim().toLowerCase();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", RESEND_TTL);

        if (Boolean.FALSE.equals(success)) {
            throw new EmailResendRateLimitException("confirmation email was sent too recently");
        }
    }

}
