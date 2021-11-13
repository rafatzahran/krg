package com.example.krg.customizedRepository;

import com.example.krg.models.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

public class UserRoleCustomizedRepositoryImpl implements UserRoleCustomizedRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRoleCustomizedRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void insertUserRoleWithQuery(UserRole userRole) {

        int pos = 0;
        entityManager.createNativeQuery("INSERT INTO user_role " +
                "(id,valid_from,valid_to,version,role_id,unit_id,user_id) VALUES (?,?,?,?,?,?,?)")
                .setParameter(++pos, userRole.getId())
                .setParameter(++pos, userRole.getValidFrom())
                .setParameter(++pos, userRole.getValidTo())
                .setParameter(++pos, userRole.getVersion())
                .setParameter(++pos, userRole.getRoleId())
                .setParameter(++pos, userRole.getUnitId())
                .setParameter(++pos, userRole.getUserId())
                .executeUpdate();
    }

    @Override
    public List<UserRole> findAllWithCreationDateTimeBefore() {
        try {
            return entityManager.createNativeQuery("SELECT ur.* FROM user_role ur where ur.valid_to is null or curdate() between valid_from and valid_to", UserRole.class).getResultList();
        } catch (NoResultException e) {
            log.warn("Failed to get valid userRole list. Ignore and continue. e = {}", e.getMessage());
        }
        return Collections.emptyList();
    }

}