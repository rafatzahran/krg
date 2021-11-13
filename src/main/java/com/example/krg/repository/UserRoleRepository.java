package com.example.krg.repository;

import com.example.krg.customizedRepository.UserRoleCustomizedRepository;
import com.example.krg.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, UserRoleCustomizedRepository {
    List<UserRole> findByUserId(Long userId);

    void insertUserRoleWithQuery(UserRole userRole);
}