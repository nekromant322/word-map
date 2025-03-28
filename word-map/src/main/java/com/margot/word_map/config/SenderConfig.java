package com.margot.word_map.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SenderProperties.class)
public class SenderConfig {

    @Bean
    public JavaMailSender getJavaMailSender(SenderProperties senderProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(senderProperties.getHost());
        mailSender.setPort(senderProperties.getPort());

        mailSender.setUsername(senderProperties.getUsername());
        mailSender.setPassword(senderProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", senderProperties.getProperties().getMail().getTransport().getProtocol());
        props.put("mail.smtp.auth", senderProperties.getProperties().getMail().getSmtp().isAuth());
        props.put("mail.smtp.starttls.enable", senderProperties.getProperties().getMail().getStarttls().isEnable());

        return mailSender;
    }
}
