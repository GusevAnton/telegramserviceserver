package ru.gusevanton.telegramnotificationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by antongusev on 18.10.17.
 */
@Data
@Component
@ConfigurationProperties(prefix = "mail")
public class MailConfig {

    private String username;

    private String password;

    private String codeMessage;

    private String subject;

}
