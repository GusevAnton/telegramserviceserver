package com.gusevanton.telegramnotificationservice;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gusevanton.telegramnotificationservice.config.ExceptionHandler;
import com.gusevanton.telegramnotificationservice.config.ProxyConfig;
import com.gusevanton.telegramnotificationservice.entity.User;
import com.gusevanton.telegramnotificationservice.service.TelegramService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetSocketAddress;
import java.net.Proxy;
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
    private ProxyConfig proxyConfig;

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
    @ConditionalOnProperty(value = "proxy.use", havingValue = "true")
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort()));
        okhttp3.Authenticator authenticator = (route, response) -> response.request().newBuilder().header("Proxy-Authorization", Credentials.basic(proxyConfig.getUsername(), proxyConfig.getPassword())).build();
        return okHttpClientBuilder.proxy(proxy).proxyAuthenticator(authenticator).build();
    }

    @Bean
    public TelegramBot telegramBot(@Autowired(required = false) OkHttpClient okHttpClient, @Value("${telegram.token}") String telegramToken) {
        TelegramBot.Builder builder = new TelegramBot.Builder(telegramToken);
        if (okHttpClient != null)
            builder.okHttpClient(okHttpClient);
        TelegramBot telegramBot = builder.build();
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
