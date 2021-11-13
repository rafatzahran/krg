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

    //@Query("select a from Article a where a.creationDateTime <= :creationDateTime")
    //List<UserRole> findAllWithCreationDateTimeBefore(
      //      @Param("creationDateTime") LocalDateTime creationDateTime);

    //@Query("SELECT ur FROM UserRole ur where valid_to is null or curdate() between valid_from and valid_to")
    List<UserRole> findAllWithCreationDateTimeBefore();
}