package com.example.krg.customizedRepository;

import com.example.krg.models.UserRole;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleCustomizedRepository {

    void insertUserRoleWithQuery(UserRole userRole);
}