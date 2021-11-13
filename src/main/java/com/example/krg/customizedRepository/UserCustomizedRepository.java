package com.example.krg.customizedRepository;

import com.example.krg.models.User;
import com.example.krg.models.UserRole;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCustomizedRepository {

    Optional<User> findByNameAndVersion(String name, Long version);
}