package com.example.krg.controllers;

import com.example.krg.models.Role;
import com.example.krg.repository.RoleRepository;
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
@RequestMapping("/api/role")
public class RoleController {
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllRoles() {
        try {
            List<Role> roles = roleRepository.findAll();
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get all roles: %s", e.getMessage()));
        }
    }

}
