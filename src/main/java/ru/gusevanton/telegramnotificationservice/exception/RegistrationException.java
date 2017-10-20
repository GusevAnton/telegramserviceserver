package ru.gusevanton.telegramnotificationservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by antongusev on 15.10.17.
 */
@Data
@AllArgsConstructor
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ошибка регистрации")
public class RegistrationException extends RuntimeException {

}
