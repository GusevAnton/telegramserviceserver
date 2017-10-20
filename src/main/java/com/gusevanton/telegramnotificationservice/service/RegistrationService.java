package com.gusevanton.telegramnotificationservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.gusevanton.telegramnotificationservice.config.TelegramConfig;
import com.gusevanton.telegramnotificationservice.entity.User;
import com.gusevanton.telegramnotificationservice.exception.ConfirmationException;
import com.gusevanton.telegramnotificationservice.repository.ServicePersistRepository;
import com.gusevanton.telegramnotificationservice.repository.UserPersistRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Created by antongusev on 15.10.17.
 */
@Service
public class RegistrationService {

    @Autowired
    @Lazy
    private TelegramBot telegramBot;

    @Autowired
    private TelegramConfig telegramConfig;

    @Autowired
    private UserPersistRepository userPersistRepository;

    @Autowired
    private SubmitService submitService;

    @Autowired
    @Qualifier("nonValidatedUsersCache")
    private Cache<Integer, User> nonValidatedUsersCache;

    @Autowired
    @Qualifier("confirmationCache")
    private Cache<Integer, String> confirmationCache;

    public Consumer<Update> register = update -> {
        User user = new User();
        user.setChatId(update.message().chat().id());
        user.setUserId(update.message().from().id());
        user.setUserName(update.message().from().username());
        nonValidatedUsersCache.put(update.message().from().id(), user);
        telegramBot.execute(new SendMessage(update.message().chat().id(), telegramConfig.getPochtabankEmailMessage()));
    };

    public Supplier<String> generateCode = () -> {
        StringBuilder code = new StringBuilder("");
        IntStream.range(0, 6).map(value -> new Random().nextInt(10)).peek(intValue -> code.append(intValue)).count();
        return code.toString();
    };

    public Consumer<Update> confirmUser = update -> {
        User user = nonValidatedUsersCache.getIfPresent(update.message().from().id());
        if (user != null && !user.isValidated()) {
            confirmationCache.invalidate(user.getUserId());
            user.setValidated(true);
            userPersistRepository.persist(user);
            telegramBot.execute(new SendMessage(update.message().chat().id(), telegramConfig.getInitialMessage()));
            return;
        }
        throw new ConfirmationException(update.message().chat().id());
    };

    @Value("${mail.codeMessage}")
    private String codeMessage;

    public Consumer<Update> sendConfirmation = update -> {
        int userId = update.message().from().id();
        User user = nonValidatedUsersCache.getIfPresent(userId);
        if (user != null) {
            user.setUserEmail(update.message().text());
            String confirmationCode = this.generateCode.get();
            confirmationCache.put(user.getUserId(), confirmationCode);
            submitService.submit(user.getUserEmail(), String.format(codeMessage, confirmationCode));
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), telegramConfig.getConfirmationSentMessage());
            telegramBot.execute(sendMessage);
        }
    };
}
