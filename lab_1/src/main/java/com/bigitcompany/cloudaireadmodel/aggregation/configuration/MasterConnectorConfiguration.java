package com.bigitcompany.cloudaireadmodel.aggregation.configuration;

import com.sap.fsm.optimization.permissions.client.CloudMasterClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MasterConnectorConfiguration {

    @Bean
    public CloudMasterClient cloudMasterClient(@Value("${service.master.host}") String masterHost,
                                               @Qualifier("userRestTemplate") RestTemplate restTemplate,
                                               @Value("${application.name}") String applicationName,
                                               @Value("${application.version}") String applicationVersion) {
        return new CloudMasterClient(masterHost, restTemplate, applicationName, applicationVersion);
    }
}
