package ru.gusevanton.telegramnotificationservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.gusevanton.telegramnotificationservice.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Created by antongusev on 15.10.17.
 */
@Repository
@Qualifier("userrepository")
public interface UserRepository extends CassandraRepository<User, Integer>, UserPersistRepository<User, Integer> {

    default User persist(User entity) {
        return this.save(entity);
    }

    default Optional<User> getById(Integer idValue) {
        return this.findById(idValue);
    }

    default List<User> getAll() {
        return this.getAll();
    }

}
