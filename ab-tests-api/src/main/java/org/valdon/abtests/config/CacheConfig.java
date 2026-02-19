package org.valdon.abtests.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public @Nullable CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis connection failed, cache was not gotten");
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, @Nullable Object value) {
                log.warn("Redis connection failed, cache was not put");
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.warn("Redis connection failed, cache was not deleted");
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.warn("Redis connection failed, cache was not cleared");
            }
        };
    }

    @Bean
    public CacheManager cacheManager(
            @Qualifier("redisConnectionFactory1") RedisConnectionFactory cf
    ) {
        RedisCacheConfiguration cfg = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60));
        return RedisCacheManager.builder(cf)
                .cacheDefaults(cfg)
                .build();
    }

}
