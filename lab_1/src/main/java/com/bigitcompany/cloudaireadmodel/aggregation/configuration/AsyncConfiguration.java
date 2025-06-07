package com.bigitcompany.cloudaireadmodel.aggregation.configuration;

import io.micrometer.context.ContextSnapshotFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {

    @Bean(name = "taskExecutorWithSecurityContext")
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor() {
        return new DelegatingSecurityContextAsyncTaskExecutor(
            new CARMContextTaskExecutor(new SimpleAsyncTaskExecutor(r -> new Thread(ContextSnapshotFactory.builder().build().captureAll().wrap(r))))
        );
    }
}
