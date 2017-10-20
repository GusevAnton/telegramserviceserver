package ru.gusevanton.telegramnotificationservice.repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by antongusev on 20.10.17.
 */
public interface ServicePersistRepository<Service, ServicePrimaryKey> {

    Service persist(Service entity);

    Optional<Service> getById(ServicePrimaryKey idValue);

    List<Service> getAll();

}
