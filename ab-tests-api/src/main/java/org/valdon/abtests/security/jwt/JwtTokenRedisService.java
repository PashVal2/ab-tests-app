package org.valdon.abtests.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtTokenRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private final static String BLACK_LIST = "blacklist:";

    public void invalidateToken(String iji, Duration ttl) {
        redisTemplate.opsForValue().set(BLACK_LIST + iji, "INVALID", ttl);
    }

    public boolean isBlackListed(String iji) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACK_LIST + iji));
    }

}
