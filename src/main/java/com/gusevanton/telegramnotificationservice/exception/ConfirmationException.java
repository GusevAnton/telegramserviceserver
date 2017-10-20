package com.gusevanton.telegramnotificationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by antongusev on 17.10.17.
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Введи код подтверждения")
public class ConfirmationException extends RuntimeException {

    public ConfirmationException(Long chatId) {
        super(String.valueOf(chatId));
    }

}
