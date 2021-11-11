package com.example.krg.repository;

import com.example.krg.models.EUnit;
import com.example.krg.models.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Optional<Unit> findByName(EUnit name);

    Boolean existsByName(EUnit name);
}