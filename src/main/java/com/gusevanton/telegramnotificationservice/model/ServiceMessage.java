package com.gusevanton.telegramnotificationservice.model;

import lombok.Data;

/**
 * Created by antongusev on 14.10.17.
 */
@Data
public class ServiceMessage {

    private String serviceName;

    private String profile;

    private String action;

    private String messageText;

    private ServiceTextMessageObject serviceTextMessageObject;

}
