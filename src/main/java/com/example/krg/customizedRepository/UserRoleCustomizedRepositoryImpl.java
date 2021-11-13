package com.example.krg.customizedRepository;

import com.example.krg.models.UserRole;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class UserRoleCustomizedRepositoryImpl implements UserRoleCustomizedRepository {

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

}