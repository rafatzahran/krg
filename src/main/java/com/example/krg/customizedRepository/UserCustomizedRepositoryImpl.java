package com.example.krg.customizedRepository;

import com.example.krg.models.User;
import com.example.krg.models.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class UserCustomizedRepositoryImpl implements UserCustomizedRepository {
    private static final Logger log = LoggerFactory.getLogger(UserCustomizedRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByNameAndVersion(String name, Long version) {
        User user = null;
        try {
            Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.name=:name AND u.version=:version");
            query.setParameter("name", name);
            query.setParameter("version", version);
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("User with name={} and version={} is already exists. Ignore and continue. e={}", name, version, e.getMessage());
        }
        return Optional.ofNullable(user);
    }

}