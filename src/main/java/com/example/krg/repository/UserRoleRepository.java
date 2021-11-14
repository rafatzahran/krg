package com.example.krg.repository;

import com.example.krg.customizedRepository.UserRoleCustomizedRepository;
import com.example.krg.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, UserRoleCustomizedRepository {
    List<UserRole> findByUserId(Long userId);

    void insertUserRoleWithQuery(UserRole userRole);

    @Query(value  =
            "SELECT * FROM user_role " +
                    "WHERE user_id = :user_id AND unit_id = :unit_id ", nativeQuery = true)
    List<UserRole> findByUserIdAndUnitId(@Param("user_id") Long userId, @Param("unit_id") Long unitId);

    @Query(value  =
            "SELECT * FROM user_role " +
                    "WHERE user_id = :user_id AND unit_id = :unit_id " +
                    "AND (valid_to IS NULL OR :datetime between valid_from and valid_to)", nativeQuery = true)
    List<UserRole> findOnlyValidGivenDateTimeUserIdAndUnitId(@Param("user_id") Long userId,
                                                             @Param("unit_id") Long unitId,
                                                             @Param("datetime") LocalDateTime dateTime);

    @Query(value  =
            "SELECT * FROM user_role WHERE user_id = :user_id", nativeQuery = true)
    List<UserRole> findAllByUserId(@Param("user_id") Long userId);

    @Query(value  =
            "SELECT * FROM user_role " +
                    "WHERE user_id = :user_id AND unit_id = :unit_id AND role_id = :role_id ", nativeQuery = true)
    List<UserRole> findByUserIdUnitIdAndRoleId(@Param("user_id") Long userId, @Param("unit_id") Long unitId, @Param("role_id") Long roleId);
}