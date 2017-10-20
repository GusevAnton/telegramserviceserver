package com.gusevanton.telegramnotificationservice.request;

import com.gusevanton.telegramnotificationservice.model.ServiceMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

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
