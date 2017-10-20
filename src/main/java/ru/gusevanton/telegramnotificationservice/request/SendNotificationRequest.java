package ru.gusevanton.telegramnotificationservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.gusevanton.telegramnotificationservice.model.ServiceMessage;

/**
 * Created by antongusev on 16.10.17.
 */
@Data
@AllArgsConstructor
public class SendNotificationRequest {

    private Long chatId;
    private ServiceMessage serviceMessage;
    private String fileName;

}
