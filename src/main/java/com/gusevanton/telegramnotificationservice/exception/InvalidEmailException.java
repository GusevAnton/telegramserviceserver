package com.gusevanton.telegramnotificationservice.exception;

/**
 * Created by antongusev on 18.10.17.
 */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(Long message) {
        super(String.valueOf(message));
    }

}
