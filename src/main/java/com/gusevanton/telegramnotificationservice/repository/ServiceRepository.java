package com.gusevanton.telegramnotificationservice.repository;

import com.gusevanton.telegramnotificationservice.entity.Service;
import com.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by antongusev on 16.10.17.
 */
@Repository
public interface ServiceRepository extends CassandraRepository<Service, ServicePrimaryKey>, ServicePersistRepository<Service, ServicePrimaryKey> {

    default Service persist(Service entity) {
        return this.save(entity);
    }

    default Optional<Service> getById(ServicePrimaryKey idValue) {
        return this.findById(idValue);
    }

    default List<Service> getAll() {
        return this.findAll();
    }

}
