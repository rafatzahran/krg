package com.example.krg.customizedRepository;

import com.example.krg.models.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleCustomizedRepository {

    void insertUserRoleWithQuery(UserRole userRole);

    List<UserRole> findAllWithCreationDateTimeBefore();
}