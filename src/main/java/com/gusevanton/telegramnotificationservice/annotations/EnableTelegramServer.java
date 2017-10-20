package com.gusevanton.telegramnotificationservice.annotations;

import com.gusevanton.telegramnotificationservice.TelegramnotificationserviceApplication;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by antongusev on 20.10.17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({TelegramnotificationserviceApplication.class})
public @interface EnableTelegramServer {
}
