package ru.gusevanton.telegramnotificationservice.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.gusevanton.telegramnotificationservice.config.TelegramConfig;
import ru.gusevanton.telegramnotificationservice.entity.User;
import ru.gusevanton.telegramnotificationservice.exception.ConfirmationException;
import ru.gusevanton.telegramnotificationservice.model.ServiceMessage;
import ru.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;
import ru.gusevanton.telegramnotificationservice.repository.ServiceRepository;
import ru.gusevanton.telegramnotificationservice.repository.UserPersistRepository;

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
    @Qualifier("servicerepository")
    private ServiceRepository serviceRepository;

    @Autowired
    @Qualifier("userrepository")
    private UserPersistRepository<User, Integer> userRepository;

    @Autowired
    private TelegramRouter telegramRouter;

    @Autowired
    private TelegramBot telegramBot;

    public final Consumer<List<Update>> router = updateList ->
        updateList.stream().forEach(update -> {
            String messageText = update.message().text();
            User user = userRepository.getById(update.message().from().id()).orElse(null);
            if (user == null) {
                telegramRouter.registrationRoute.accept(update);
                return;
            }
            if (!user.isValidated()) {
                throw new ConfirmationException(update.message().chat().id());
            }
            String serviceName = this.getServiceName.apply(messageText);
            ru.gusevanton.telegramnotificationservice.entity.Service service = null;
            if (serviceName != null) {
                String[] serviceNameWithProfile = serviceName.split(":");
                ServicePrimaryKey servicePrimaryKey = new ServicePrimaryKey(serviceNameWithProfile[0], serviceNameWithProfile[1]);
                service = serviceRepository.findById(servicePrimaryKey).orElse(null);
            }
            if (service != null) {
                telegramRouter.serviceRoute.accept(service, update);
            } else if (telegramConfig.getInitialMessage().equals(messageText))
                this.startConsumer.accept(update);
            else if (telegramConfig.getMainMenuFirstOption().equals(messageText))
                this.logConsumer.accept(update);
            else if (telegramConfig.getMainMenuSecondOption().equals(messageText))
                this.logConsumer.accept(update);
            else
                this.defaultConsumer.accept(update);
        });

    private final Consumer<Update> defaultConsumer = update -> {
        Long chatId = update.message().chat().id();
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getInitialMessage());
        telegramBot.execute(sendMessage);
    };

    private final Consumer<Update> logConsumer = update -> {
        List<ru.gusevanton.telegramnotificationservice.entity.Service> serviceList = serviceRepository.findAll();
        KeyboardButton[] keyboardButtonArray = new KeyboardButton[serviceList.size()];
        AtomicInteger i = new AtomicInteger(0);
        serviceList.parallelStream().map(service -> new KeyboardButton("/service register " + service.getServicePrimaryKey().getServiceName() + ":" + service.getServicePrimaryKey().getProfile())).forEach(keyboardButton -> keyboardButtonArray[i.getAndIncrement()] = keyboardButton);
        Long chatId = update.message().chat().id();
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(keyboardButtonArray);
        keyboard.oneTimeKeyboard(true);
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getChooseOption());
        sendMessage.replyMarkup(keyboard);
        telegramBot.execute(sendMessage);
    };

    private final Consumer<Update> startConsumer = update -> {
        Long chatId = update.message().chat().id();
        Keyboard keyboard = new ReplyKeyboardMarkup(new KeyboardButton[]{
                new KeyboardButton(telegramConfig.getMainMenuFirstOption()),
                new KeyboardButton(telegramConfig.getMainMenuSecondOption())
        });
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
