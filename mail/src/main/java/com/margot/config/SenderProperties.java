package com.margot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class SenderProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private MailPropertiesDetails properties;

    @Data
    public static class MailPropertiesDetails {
        private MailSmtp mail;
    }

    @Data
    public static class MailSmtp {
        private Transport transport;
        private Smtp smtp;
        private MailStarttls starttls;
    }

    @Data
    public static class Smtp {
        private boolean auth;
    }

    @Data
    public static class Transport {
        private String protocol;
    }

    @Data
    public static class MailStarttls {
        private boolean enable;
        private boolean required;
    }
}
