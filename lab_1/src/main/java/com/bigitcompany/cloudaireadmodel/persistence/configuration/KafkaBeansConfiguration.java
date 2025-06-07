package com.bigitcompany.cloudaireadmodel.persistence.configuration;

import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.persistence.events.filters.TenantBasedEventFilter;
import com.sap.fsm.springboot.starter.events.infrastructure.event.EventAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "starter-events.kafka.enabled", havingValue = "true")
@AutoConfigureBefore(value = EventAutoConfiguration.class)
public class KafkaBeansConfiguration {

    @Bean
    public TenantBasedEventFilter readModelTenantEventFilter(ActiveTenantProvider provider) {
        return new TenantBasedEventFilter(provider);
    }
}