package com.gusevanton.telegramnotificationservice.repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by antongusev on 20.10.17.
 */
public interface UserPersistRepository<User, Integer> {

    User persist(User entity);

    Optional<User> getById(Integer idValue);

    List<User> getAll();

}
