package com.example.krg.repository;

import com.example.krg.customizedRepository.UserCustomizedRepository;
import com.example.krg.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomizedRepository {
    List<User> findByName(String name);

    Optional<User> findByNameAndVersion(String name, Long version);

    Boolean existsByName(String name);

    @Query(value  =
            "SELECT u.* FROM user u " +
                    "LEFT JOIN user_role ur ON u.id = ur.user_id " +
                    "WHERE unit_id = :unit_id " +
                    "AND (valid_to IS NULL OR :datetime between valid_from and valid_to) " +
                    "GROUP BY u.id", nativeQuery = true)
    List<User> findAllValidUsersGivenUnitAndDateTime(@Param("unit_id") Long unitId, @Param("datetime") LocalDateTime dateTime);
}