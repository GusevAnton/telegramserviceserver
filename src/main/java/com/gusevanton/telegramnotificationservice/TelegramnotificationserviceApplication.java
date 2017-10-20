package com.gusevanton.telegramnotificationservice;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gusevanton.telegramnotificationservice.config.ExceptionHandler;
import com.gusevanton.telegramnotificationservice.entity.User;
import com.gusevanton.telegramnotificationservice.service.TelegramService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication(scanBasePackages = {
        "com.gusevanton.telegramnotificationservice.config",
        "com.gusevanton.telegramnotificationservice.endpoint",
        "com.gusevanton.telegramnotificationservice.mapper",
        "com.gusevanton.telegramnotificationservice.service"
})
@EnableScheduling
public class TelegramnotificationserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelegramnotificationserviceApplication.class, args);
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
        return Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
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
