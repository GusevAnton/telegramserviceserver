package ru.gusevanton.telegramnotificationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by antongusev on 14.10.17.
 */
@Data
@Component
@ConfigurationProperties("telegram")
public class TelegramConfig {

    private String initialMessage;

    private String confirmationErrorMessage;

    private String registerMessage;

    private String mainMenuFirstOption;

    private String mainMenuSecondOption;

    private String chooseOption;

    private String mainMenuRegistrationOption;

    private String pochtabankEmailMessage;

    public String confirmationSentMessage;

    private String emailSuffix;

}
