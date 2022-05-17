package com.example.krg.repository;

import com.example.krg.models.ERole;
import com.example.krg.models.EUnit;
import com.example.krg.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

    Boolean existsByName(EUnit name);
}