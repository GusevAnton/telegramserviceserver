package ru.gusevanton.telegramnotificationservice.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gusevanton.telegramnotificationservice.exception.AuthorizationException;
import ru.gusevanton.telegramnotificationservice.exception.ConfirmationException;
import ru.gusevanton.telegramnotificationservice.exception.InvalidEmailException;

import java.util.function.Consumer;

/**
 * Created by antongusev on 17.10.17.
 */
@Component
public class ExceptionHandler {

    @Autowired
    private TelegramConfig telegramConfig;

    @Autowired
    private TelegramBot telegramBot;

    public Consumer<RuntimeException> handle = runtimeException -> {
        if (runtimeException instanceof AuthorizationException)
            this.authConsumer.accept(runtimeException.getMessage());
        else if (runtimeException instanceof ConfirmationException)
            this.confirmationConsumer.accept(runtimeException.getMessage());
        else if (runtimeException instanceof InvalidEmailException)
            this.invalidEmailConsumer.accept(runtimeException.getMessage());
    };

    private final Consumer<String> invalidEmailConsumer = (chatId) -> {
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getPochtabankEmailMessage());
        telegramBot.execute(sendMessage);
    };

    private final Consumer<String> confirmationConsumer = (chatId) -> {
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getConfirmationErrorMessage());
        telegramBot.execute(sendMessage);
    };

    private final Consumer<String> authConsumer = (chatId) -> {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(new KeyboardButton[]{new KeyboardButton(telegramConfig.getRegisterMessage())});
        keyboard.oneTimeKeyboard(true);
        SendMessage sendMessage = new SendMessage(chatId, telegramConfig.getChooseOption());
        sendMessage.replyMarkup(keyboard);
        telegramBot.execute(sendMessage);
    };

}
