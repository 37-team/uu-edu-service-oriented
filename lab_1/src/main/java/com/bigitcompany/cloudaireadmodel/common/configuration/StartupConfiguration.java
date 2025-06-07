package com.bigitcompany.cloudaireadmodel.common.configuration;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class StartupConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        // forces hibernate read database dates in UTC
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }
}