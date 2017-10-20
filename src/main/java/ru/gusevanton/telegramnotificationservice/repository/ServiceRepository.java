package ru.gusevanton.telegramnotificationservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.gusevanton.telegramnotificationservice.entity.Service;
import ru.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;

import java.util.List;
import java.util.Optional;

/**
 * Created by antongusev on 16.10.17.
 */
@Repository
@Qualifier("servicerepository")
public interface ServiceRepository extends CassandraRepository<Service, ServicePrimaryKey>, ServicePersistRepository<Service, ServicePrimaryKey> {

    default Service persist(Service entity) {
        return this.save(entity);
    }

    default Optional<Service> getById(ServicePrimaryKey idValue) {
        return this.findById(idValue);
    }

    default List<Service> getAll() {
        return this.getAll();
    }

}
