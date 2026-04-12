package org.valdon.abtests.pubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.valdon.abtests.dto.user.UserCreatedEvent;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic topic;

    public void publishUserEvent(UserCreatedEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }

}
