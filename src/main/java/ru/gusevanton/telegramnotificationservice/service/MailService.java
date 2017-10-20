package ru.gusevanton.telegramnotificationservice.service;

import lombok.extern.slf4j.Slf4j;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gusevanton.telegramnotificationservice.config.MailConfig;

import java.util.function.BiConsumer;

/**
 * Created by antongusev on 18.10.17.
 */
@Slf4j
@Service
public class MailService implements SubmitService {

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private MailConfig mailConfig;

    private BiConsumer<String, String> sendMail = (emailAddressTo, messageText) -> {
        try {
            EmailMessage msg= new EmailMessage(exchangeService);
            msg.setSubject(mailConfig.getSubject());
            msg.setBody(MessageBody.getMessageBodyFromText(messageText));
            msg.getToRecipients().add(emailAddressTo);
            msg.setFrom(new EmailAddress(mailConfig.getUsername()));
            msg.send();
        } catch (Exception e) {
            log.error("{}", e);
        }
    };

    @Override
    public void submit(String messageTo, String messageText) {
        this.sendMail.accept(messageTo, messageText);
    }
}
