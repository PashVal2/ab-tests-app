package org.valdon.abtests.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.valdon.abtests.config.props.RedisProperties;
import org.valdon.abtests.pubsub.RedisMessageSubscriber;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@AllArgsConstructor
@Configuration
public class RedisConfig {

    private final RedisProperties redisProps;

    @Bean // RedisTemplate
    @Primary
    public RedisConnectionFactory redisConnectionFactory0() {
        return createRedisConnectionFactory(0);
    }

    @Bean // CacheManager
    public RedisConnectionFactory redisConnectionFactory1() {
        return createRedisConnectionFactory(1);
    }

    @Bean // PubSub
    public RedisConnectionFactory redisConnectionFactory2() {
        return createRedisConnectionFactory(2);
    }

    private LettuceConnectionFactory createRedisConnectionFactory(int db) {
        var cfg = new RedisStandaloneConfiguration();
        cfg.setPassword(redisProps.getPassword());
        cfg.setHostName(redisProps.getHost());
        cfg.setPort(Integer.parseInt(redisProps.getPort()));
        cfg.setDatabase(db);
        return new LettuceConnectionFactory(cfg);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(
            @Qualifier("redisConnectionFactory0") RedisConnectionFactory cf
    ) {
        final RedisTemplate<String, String> rt = new RedisTemplate<>();
        rt.setKeySerializer(new StringRedisSerializer());
        rt.setValueSerializer(new StringRedisSerializer());
        rt.setConnectionFactory(cf);
        return rt;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplatePubSub(
            @Qualifier("redisConnectionFactory2") RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        template.setValueSerializer(new GenericJacksonJsonRedisSerializer(objectMapper));
        return template;
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("user-created-topic");
    }

    @Bean
    RedisMessageListenerContainer redisContainer(
            @Qualifier("redisConnectionFactory2") RedisConnectionFactory connectionFactory,
            RedisMessageSubscriber subscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(subscriber, topic());
        return container;
    }

}
