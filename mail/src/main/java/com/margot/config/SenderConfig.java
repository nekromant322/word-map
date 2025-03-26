package com.margot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class SenderConfig {

    @Value("${spring.mail.host}")
    private String hostFromConfig;

    @Value("${spring.mail.username}")
    private String mailFromConfig;

    @Value("${spring.mail.password}")
    private String passwordFromConfig;
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(hostFromConfig);
        mailSender.setPort(587);

        mailSender.setUsername(mailFromConfig);
        mailSender.setPassword(passwordFromConfig);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
