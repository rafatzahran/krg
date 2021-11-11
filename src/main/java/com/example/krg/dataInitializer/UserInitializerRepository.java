package com.example.krg.dataInitializer;

import com.example.krg.models.User;
import com.example.krg.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UserInitializerRepository implements ApplicationRunner {

    @Autowired
    UserRepository repo;

    @Override
    public void run(ApplicationArguments args) {

        repo.saveAll(Arrays.asList(
                new User(1L, "Alice", 1L),
                new User(2L, "Bob", 2L),
                new User(3L, "Eve", 1L)
        ));
    }
}