package com.example.krg.controllers;

import com.example.krg.exception.BadRequestException;
import com.example.krg.exception.NotFoundException;
import com.example.krg.models.EUnit;
import com.example.krg.models.Role;
import com.example.krg.models.User;
import com.example.krg.models.UserRole;
import com.example.krg.repository.RoleRepository;
import com.example.krg.repository.UnitRepository;
import com.example.krg.repository.UserRepository;
import com.example.krg.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8087")
@RestController
@RequestMapping("/api/userrole")
public class UserRoleController {
    private static final Logger log = LoggerFactory.getLogger(UserRoleController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @GetMapping("/all/userid/{userId}/unitid/{unitId}")
    public ResponseEntity<Object> getAllValidUsersGivenUnitAndDateTime(@PathVariable(required = true) Long userId,
                                                                           @PathVariable(required = true) Long unitId) {
        Map<String, String> errorResponse = new HashMap<>();
        if (userId == null) {
            errorResponse.put("message", "UserId must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if (unitId == null) {
            errorResponse.put("message", "UnitId must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            errorResponse.put("message", String.format("User with id = %s not found.", userId));
            errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        boolean unitExists = unitRepository.existsById(unitId);
        if (!unitExists) {
            errorResponse.put("message", String.format("Unit with id = %s not found.", unitId));
            errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        try {
            List<UserRole> userRoles = userRoleRepository.findByUserIdAndUnitId(userId, unitId);

            if (userRoles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userRoles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
