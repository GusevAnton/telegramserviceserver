package ru.gusevanton.telegramnotificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gusevanton.telegramnotificationservice.request.SendNotificationRequest;

import java.util.function.Consumer;

/**
 * Created by antongusev on 14.10.17.
 */
@Service
public class NotificationService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TelegramBot telegramBot;

    public final Consumer<SendNotificationRequest> sendNotification = sendNotificationRequest -> telegramBot.execute(new SendMessage(sendNotificationRequest.getChatId(), sendNotificationRequest.getServiceMessage().getMessageText()));

    public final Consumer<SendNotificationRequest> sendNotificationDocument = sendNotificationRequest -> {
        byte[] byteArr = null;
        try {
            byteArr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(sendNotificationRequest.getServiceMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (byteArr != null) {
            SendDocument sendDocument = new SendDocument(sendNotificationRequest.getChatId(), byteArr);
            sendDocument.fileName(sendNotificationRequest.getFileName());
            telegramBot.execute(sendDocument);
        }
    };

}
