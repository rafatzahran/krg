package com.example.krg.dataInitializer;

import com.example.krg.models.ERole;
import com.example.krg.models.Role;
import com.example.krg.models.User;
import com.example.krg.repository.RoleRepository;
import com.example.krg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RoleInitializerRepository implements ApplicationRunner {

    @Autowired
    RoleRepository repo;

    @Override
    public void run(ApplicationArguments args) {

        repo.saveAll(Arrays.asList(
                new Role(101L, ERole.USER_ADMINISTRATION, 1L),
                new Role(102L, ERole.ENDOSCOPIST_ADMINISTRATION, 2L),
                new Role(103L, ERole.REPORT_COLONOSCOPY_CAPACITY, 1L),
                new Role(104L, ERole.SEND_INVITAIONS, 2L),
                new Role(105L, ERole.VIEW_STATISTICS, 1L)
        ));
    }
}