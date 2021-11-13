package com.example.krg.repository;

import com.example.krg.customizedRepository.UserRoleCustomizedRepository;
import com.example.krg.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, UserRoleCustomizedRepository {
    List<UserRole> findByUserId(Long userId);

    void insertUserRoleWithQuery(UserRole userRole);

    @Query(value  =
            "SELECT * FROM user_role " +
                    "WHERE user_id = :user_id AND unit_id = :unit_id ", nativeQuery = true)
    List<UserRole> findByUserIdAndUnitId(@Param("user_id") Long userId, @Param("unit_id") Long unitId);
}