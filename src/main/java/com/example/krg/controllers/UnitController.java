package com.example.krg.controllers;

import com.example.krg.models.Unit;
import com.example.krg.repository.UnitRepository;
import com.example.krg.repository.UserRepository;
import com.example.krg.repository.UserRoleRepository;
import com.example.krg.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8087")
@RestController
@RequestMapping("/api/unit")
public class UnitController {
    private static final Logger log = LoggerFactory.getLogger(UnitController.class);

    @Autowired
    UnitRepository unitRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUnits() {
        try {
            List<Unit> units = unitRepository.findAll();
            return new ResponseEntity<>(units, HttpStatus.OK);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get all units: %s", e.getMessage()));
        }
    }

}
