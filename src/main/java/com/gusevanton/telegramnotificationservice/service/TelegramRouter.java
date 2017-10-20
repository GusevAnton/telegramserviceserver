package com.gusevanton.telegramnotificationservice.service;

import com.gusevanton.telegramnotificationservice.config.TelegramConfig;
import com.gusevanton.telegramnotificationservice.entity.Service;
import com.gusevanton.telegramnotificationservice.exception.AuthorizationException;
import com.gusevanton.telegramnotificationservice.exception.InvalidEmailException;
import com.gusevanton.telegramnotificationservice.repository.ServicePersistRepository;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Created by antongusev on 20.10.17.
 */
@Component
public class TelegramRouter {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private TelegramConfig telegramConfig;

    private final static Pattern digitsPattern = Pattern.compile("\\d+");

    private final static Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    @Autowired
    private ServicePersistRepository servicePersistRepository;

    public Consumer<Update> registrationRoute = update -> {
        String messageText = update.message().text();
        if (telegramConfig.getRegisterMessage().equals(messageText)) {
            registrationService.register.accept(update);
        } else if (EMAIL_PATTERN.matcher(messageText).matches()) {
            if (!messageText.contains(telegramConfig.getEmailSuffix()))
                throw new InvalidEmailException(update.message().chat().id());
            registrationService.sendConfirmation.accept(update);
        } else if (messageText.length() == 6 && digitsPattern.matcher(messageText).matches()) {
            registrationService.confirmUser.accept(update);
        } else
            throw new AuthorizationException(update.message().chat().id());
    };

    public BiConsumer<Service, Update> serviceRoute = (service, update) -> {
        String messageText = update.message().text();
        if (messageText.contains("register")) {
            if (service.getChatIdSet() == null)
                service.setChatIdSet(new HashSet<>());
            service.getChatIdSet().add(update.message().chat().id());
            servicePersistRepository.persist(service);
        } else if (messageText.contains("unregister")) {

        }
    };



}
