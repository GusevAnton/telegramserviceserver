package ru.gusevanton.telegramnotificationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by antongusev on 17.10.17.
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Ошибка авторизации")
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(Long chatId) {
        super(String.valueOf(chatId));
    }

}
