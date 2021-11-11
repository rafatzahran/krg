package com.example.krg.dataInitializer;

import com.example.krg.models.EUnit;
import com.example.krg.models.Unit;
import com.example.krg.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class UnitInitializerRepository implements ApplicationRunner {

    @Autowired
    UnitRepository repo;

    @Override
    public void run(ApplicationArguments args) {

        repo.saveAll(Arrays.asList(
                new Unit(11L, EUnit.KREFTREGISTERET, 2L),
                new Unit(12L, EUnit.AKERSHUS_HF, 1L),
                new Unit(13L, EUnit.SOUTH_HF, 2L),
                new Unit(14L, EUnit.WEST_HF, 2L)
        ));
    }
}