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
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.valdon.abtests.props.RedisProperties;

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
        log.warn("bbbbbbbbbbbbbbbb");
        final RedisTemplate<String, String> rt = new RedisTemplate<>();
        rt.setKeySerializer(new StringRedisSerializer());
        rt.setValueSerializer(new StringRedisSerializer());
        rt.setConnectionFactory(cf);
        return rt;
    }

}
