package com.example.krg.repository;

import com.example.krg.customizedRepository.UserCustomizedRepository;
import com.example.krg.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserCustomizedRepository {
    List<User> findByName(String name);

    Optional<User> findByNameAndVersion(String name, Long version);

    Boolean existsByName(String name);
}