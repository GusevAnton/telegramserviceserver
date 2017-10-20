package ru.gusevanton.telegramnotificationservice.mapper;

import org.springframework.stereotype.Component;
import ru.gusevanton.telegramnotificationservice.entity.Service;
import ru.gusevanton.telegramnotificationservice.model.ServiceMessage;
import ru.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;

import java.util.function.Function;

/**
 * Created by antongusev on 16.10.17.
 */
@Component
public class ServiceMessageToServiceMapper {

    public final Function<ServiceMessage, Service> map = serviceMessage -> {
        Service service = new Service();
        ServicePrimaryKey servicePrimaryKey = new ServicePrimaryKey(serviceMessage.getServiceName(), serviceMessage.getProfile());
        service.setServicePrimaryKey(servicePrimaryKey);
        service.setActionName(serviceMessage.getAction());
        service.setActive(true);
        return service;
    };

}
