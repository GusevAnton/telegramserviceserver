package com.gusevanton.telegramnotificationservice.repository;

import com.gusevanton.telegramnotificationservice.entity.User;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by antongusev on 15.10.17.
 */
@Repository
public interface UserRepository extends CassandraRepository<User, Integer>, UserPersistRepository<User, Integer> {

    default User persist(User entity) {
        return this.save(entity);
    }

    default Optional<User> getById(Integer idValue) {
        return this.findById(idValue);
    }

    default List<User> getAll() {
        return this.findAll();
    }

}
