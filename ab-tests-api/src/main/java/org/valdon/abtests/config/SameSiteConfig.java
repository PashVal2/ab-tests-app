package org.valdon.abtests.config;

import org.springframework.boot.web.server.servlet.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SameSiteConfig {

    @Bean
    public CookieSameSiteSupplier sameSiteSupplier() {
        return CookieSameSiteSupplier.ofStrict();
    }

}