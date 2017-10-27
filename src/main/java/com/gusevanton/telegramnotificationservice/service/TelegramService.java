package com.gusevanton.telegramnotificationservice.service;

import com.gusevanton.telegramnotificationservice.config.TelegramConfig;
import com.gusevanton.telegramnotificationservice.entity.User;
import com.gusevanton.telegramnotificationservice.exception.ConfirmationException;
import com.gusevanton.telegramnotificationservice.model.ServiceMessage;
import com.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;
import com.gusevanton.telegramnotificationservice.repository.ServicePersistRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.gusevanton.telegramnotificationservice.repository.UserPersistRepository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by antongusev on 15.10.17.
 */
@Service
public class TelegramService {

    @Autowired
    private TelegramConfig telegramConfig;

    @Autowired
    private ServicePersistRepository<com.gusevanton.telegramnotificationservice.entity.Service, ServicePrimaryKey> serviceRepository;

    @Autowired
    private UserPersistRepository<User, Integer> userRepository;

    @Autowired
    private TelegramRouter telegramRouter;

    @Autowired
    @Lazy
    private TelegramBot telegramBot;

    public final Consumer<List<Update>> router = updateList ->
        updateList.stream().forEach(update -> {
            String messageText = update.message().text();
            User user = null;
            try {
                user = userRepository.getById(update.message().from().id()).orElse(null);
            } catch (Exception e) {

            }
            if (user == null) {
                telegramRouter.registrationRoute.accept(update);
                return;
            }
            if (!user.isValidated()) {
                throw new ConfirmationException(update.message().chat().id());
            }
            String serviceName = this.getServiceName.apply(messageText);
            com.gusevanton.telegramnotificationservice.entity.Service service = null;
            if (serviceName != null) {
                String[] serviceNameWithProfile = serviceName.split(":");
                ServicePrimaryKey servicePrimaryKey = new ServicePrimaryKey(serviceNameWithProfile[0], serviceNameWithProfile[1]);
                service = serviceRepository.getById(servicePrimaryKey).orElse(null);
            }
            if (service != null) {
                telegramRouter.serviceRoute.accept(service, update);
            } else if (telegramConfig.getInitialMessage().equals(messageText))
                this.startConsumer.accept(update);
            else if (telegramConfig.getMainMenuFirstOption().equals(messageText))
                this.logConsumer.accept(update, "register");
            else if (telegramConfig.getMainMenuSecondOption().equals(messageText))
                this.logConsumer.accept(update, "unregister");
            else
                this.defaultConsumer.accept(update);
        });

    private final Consumer<Update> defaultConsumer = update -> {
        Long chatId = update.message().chat().id();
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getInitialMessage());
        telegramBot.execute(sendMessage);
    };

    private final BiConsumer<Update, String> logConsumer = (update, value) -> {
        List<com.gusevanton.telegramnotificationservice.entity.Service> serviceList = serviceRepository.getAll();
        String[][] keyboardButtonArray = new String[serviceList.size()][];
        AtomicInteger i = new AtomicInteger(0);
        serviceList.parallelStream().map(service -> "/service " + value + " " + service.getServicePrimaryKey().getServiceName() + ":" + service.getServicePrimaryKey().getProfile()).forEach(keyboardButton -> keyboardButtonArray[i.getAndIncrement()] = new String[]{keyboardButton});
        Long chatId = update.message().chat().id();
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(keyboardButtonArray, true, false, false);
        keyboard.oneTimeKeyboard(true);
        keyboard.resizeKeyboard(false);
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getChooseOption());
        sendMessage.replyMarkup(keyboard);
        telegramBot.execute(sendMessage);
    };

    private final Consumer<Update> startConsumer = update -> {
        Long chatId = update.message().chat().id();
        String[][] values = new String[2][];
        values[0] = new String[]{telegramConfig.getMainMenuFirstOption()};
        values[1] = new String[]{telegramConfig.getMainMenuSecondOption()};
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(values);
        keyboard.resizeKeyboard(false);
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getChooseOption());
        sendMessage.replyMarkup(keyboard);
        telegramBot.execute(sendMessage);
    };

    private final Function<String, String> getServiceName = messageText -> {
        String[] wordList = messageText.split(" ");
        if (wordList.length == 3) {
            return wordList[2];
        }
        return null;
    };

    public final BiConsumer<Set<Long>, ServiceMessage> notifyAboutServiceAction = (chatIdSet, serviceMessage) -> {
        if (chatIdSet != null) {
            chatIdSet.forEach(chatId -> {
                String textMessage = "Сервис " + serviceMessage.getServiceName() + " в профиле " + serviceMessage.getProfile() + " перешел в состояние " + serviceMessage.getAction();
                SendMessage sendMessage = new SendMessage(chatId, textMessage);
                telegramBot.execute(sendMessage);
            });
        }
    };

}
