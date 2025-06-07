package com.bigitcompany.cloudaireadmodel.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * As of Spring Framework 6.0 (Spring Boot 3.x.x), the trailing slash matching configuration option has been deprecated and its default value set to false
 * <a href="https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#spring-mvc-and-webflux-url-matching-changes">More info in the Spring Boot Migration Guide</a>
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseTrailingSlashMatch(true);
    }
}
