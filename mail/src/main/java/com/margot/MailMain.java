package com.margot;

import com.margot.config.SenderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SenderProperties.class)
public class MailMain {
    public static void main(String[] args) {
        SpringApplication.run(MailMain.class);
    }
}