package com.gusevanton.telegramnotificationservice.endpoint;

import com.gusevanton.telegramnotificationservice.entity.Service;
import com.gusevanton.telegramnotificationservice.mapper.ServiceMessageToServiceMapper;
import com.gusevanton.telegramnotificationservice.model.ServiceMessage;
import com.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;
import com.gusevanton.telegramnotificationservice.repository.ServicePersistRepository;
import com.gusevanton.telegramnotificationservice.request.SendNotificationRequest;
import com.gusevanton.telegramnotificationservice.service.NotificationService;
import com.gusevanton.telegramnotificationservice.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
    private ServicePersistRepository<Service, ServicePrimaryKey> serviceRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TelegramService telegramService;

    @PostMapping("/register")
    public Mono registerService(@RequestBody ServiceMessage serviceMessage) {
        return Mono.just(serviceMessage)
                .map(serviceMessageToServiceMapper.map::apply)
                .map(service -> {
                    Service persistedService = serviceRepository.getById(service.getServicePrimaryKey()).orElse(null);
                    if (persistedService == null) {
                        return serviceRepository.persist(service);
                    } else {
                        persistedService.setActive(true);
                        return serviceRepository.persist(persistedService);
                    }
                })
                .doOnNext(service -> telegramService.notifyAboutServiceAction.accept(service.getChatIdSet(), serviceMessage));
    }

    @PostMapping("/unregister")
    public Mono unregisterService(@RequestBody ServiceMessage serviceMessage) {
        return Mono.just(serviceMessage)
                .map(message -> new ServicePrimaryKey(message.getServiceName(), serviceMessage.getProfile()))
                .map(primaryKey -> serviceRepository.getById(primaryKey).get())
                .doOnNext(service -> service.setActive(false))
                .doOnNext(service -> serviceRepository.persist(service))
                .doOnNext(service -> telegramService.notifyAboutServiceAction.accept(service.getChatIdSet(), serviceMessage));
    }

    @PostMapping("/notify")
    private void postNotification(@RequestBody ServiceMessage message) {
        Mono.just(message)
                .map(serviceMessage -> new ServicePrimaryKey(serviceMessage.getServiceName(), serviceMessage.getProfile()))
                .map(primaryKey -> serviceRepository.getById(primaryKey))
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
