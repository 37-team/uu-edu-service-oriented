package com.bigitcompany.cloudaireadmodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class CloudAiReadModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudAiReadModelApplication.class, args);
    }
}