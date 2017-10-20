package ru.gusevanton.telegramnotificationservice.model;

import lombok.Data;

/**
 * Created by antongusev on 19.10.17.
 */
@Data
public class ServiceTextMessageObject {

    private String hostName;

    private Exception exception;

}
