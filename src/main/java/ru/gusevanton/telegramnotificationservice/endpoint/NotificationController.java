package ru.gusevanton.telegramnotificationservice.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.gusevanton.telegramnotificationservice.entity.Service;
import ru.gusevanton.telegramnotificationservice.mapper.ServiceMessageToServiceMapper;
import ru.gusevanton.telegramnotificationservice.model.ServiceMessage;
import ru.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;
import ru.gusevanton.telegramnotificationservice.repository.ServiceRepository;
import ru.gusevanton.telegramnotificationservice.request.SendNotificationRequest;
import ru.gusevanton.telegramnotificationservice.service.NotificationService;
import ru.gusevanton.telegramnotificationservice.service.TelegramService;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by antongusev on 14.10.17.
 */
@RestController
@RequestMapping("/telegramnotificationservice")
public class NotificationController {

    @Autowired
    private ServiceMessageToServiceMapper serviceMessageToServiceMapper;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TelegramService telegramService;

    @PostMapping("/register")
    public Mono registerService(@RequestBody ServiceMessage serviceMessage) {
        return Mono.just(serviceMessage)
                .map(serviceMessageToServiceMapper.map::apply)
                .map(service -> {
                    Service persistedService = serviceRepository.findById(service.getServicePrimaryKey()).orElse(null);
                    if (persistedService == null) {
                        return serviceRepository.save(service);
                    } else {
                        persistedService.setActive(true);
                        return serviceRepository.save(persistedService);
                    }
                })
                .doOnNext(service -> telegramService.notifyAboutServiceAction.accept(service.getChatIdSet(), serviceMessage));
    }

    @PostMapping("/unregister")
    public Mono unregisterService(@RequestBody ServiceMessage serviceMessage) {
        return Mono.just(serviceMessage)
                .map(message -> new ServicePrimaryKey(message.getServiceName(), serviceMessage.getProfile()))
                .map(primaryKey -> serviceRepository.findById(primaryKey).get())
                .doOnNext(service -> service.setActive(false))
                .doOnNext(service -> serviceRepository.save(service))
                .doOnNext(service -> telegramService.notifyAboutServiceAction.accept(service.getChatIdSet(), serviceMessage));
    }

    @PostMapping("/notify")
    private void postNotification(@RequestBody ServiceMessage message) {
        Mono.just(message)
                .map(serviceMessage -> new ServicePrimaryKey(serviceMessage.getServiceName(), serviceMessage.getProfile()))
                .map(primaryKey -> serviceRepository.findById(primaryKey))
                .map(optionalService -> optionalService.orElse(new Service()))
                .map(service -> {
                    if (service.getChatIdSet() == null) {
                        service.setChatIdSet(new HashSet<>());
                    }
                    return service.getChatIdSet();
                })
                .flatMapMany(chatIdSet -> {
                    Iterator<Long> iterator = chatIdSet.iterator();
                    if (iterator.hasNext()) {
                        return Mono.just(iterator.next());
                    }
                    return Mono.just(-1L);
                })
                .map(chatId -> new SendNotificationRequest(chatId, message, message.getServiceName() + "(" + message.getProfile() + ").txt"))
                .subscribe(sendNotificationResponse -> {
                    if (sendNotificationResponse.getChatId() >= 0)
                        notificationService.sendNotificationDocument.accept(sendNotificationResponse);
                });
    }
}
