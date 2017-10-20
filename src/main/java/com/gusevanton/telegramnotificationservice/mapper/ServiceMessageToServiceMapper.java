package com.gusevanton.telegramnotificationservice.mapper;

import com.gusevanton.telegramnotificationservice.model.ServiceMessage;
import com.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;
import org.springframework.stereotype.Component;
import com.gusevanton.telegramnotificationservice.entity.Service;

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
