package ru.gusevanton.telegramnotificationservice;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.gusevanton.telegramnotificationservice.config.ExceptionHandler;
import ru.gusevanton.telegramnotificationservice.config.MailConfig;
import ru.gusevanton.telegramnotificationservice.entity.User;
import ru.gusevanton.telegramnotificationservice.service.TelegramService;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class TelegramnotificationserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramnotificationserviceApplication.class, args);
    }

    @Autowired
    private MailConfig mailConfig;

    @Bean
    public ExchangeService exchangeService() throws Exception {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        ExchangeCredentials credentials = new WebCredentials(mailConfig.getUsername(), mailConfig.getPassword());
        service.setCredentials(credentials);
        service.autodiscoverUrl(mailConfig.getUsername(), s -> true);
        return service;
    }

    @Autowired
    private TelegramService telegramService;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Bean
    @Qualifier("nonValidatedUsersCache")
    public Cache<Integer, User> nonValidatedUsers() {
        return Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
    }

    @Bean
    @Qualifier("confirmationCache")
    public Cache<Integer, String> confirmationCache() {
        return Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();
    }

    @Bean
    @Value("${telegram.token}")
    public TelegramBot telegramBot(String telegramToken) {
        TelegramBot telegramBot = new TelegramBot(telegramToken);
        telegramBot.setUpdatesListener((updates) -> {
            try {
                telegramService.router.accept(updates);
            } catch (RuntimeException e) {
                exceptionHandler.handle.accept(e);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        return telegramBot;
    }

}
