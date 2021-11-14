package com.example.krg.controllers;

import com.example.krg.models.EUnit;
import com.example.krg.models.Unit;
import com.example.krg.models.User;
import com.example.krg.models.UserRole;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8087")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String username) {
        try {
            List<User> users = new ArrayList<User>();

            if (username == null)
                userRepository.findAll().forEach(users::add);
            else
                userRepository.findByName(username).forEach(users::add);

            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/valid/unit/{unit}/dateTime/{dateTime}")
    public ResponseEntity<?> getAllValidUsersGivenUnitAndDateTime(@PathVariable(required = true) EUnit unit,
                                                                  @PathVariable(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateTime) {
        Map<String, String> errorResponse = new HashMap<>();
        if (unit == null) {
            errorResponse.put("message", "Unit must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if (dateTime == null) {
            errorResponse.put("message", "Datetime must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            Optional<Unit> unitToUse = unitRepository.findByName(unit);

            if (!unitToUse.isPresent()) {
                errorResponse.put("message", String.format("Unit %s not found.", unit.name()));
                errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            List<User> userRoleList = userRepository.findAllValidUsersGivenUnitAndDateTime(unitToUse.get().getId(), dateTime);
            if (userRoleList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userRoleList, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("Failed to get valid users: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createUser(@RequestBody User userPostRequest) {
        Map<String, String> errorResponse = new HashMap<>();
        if (userPostRequest == null) {
            errorResponse.put("message", "User to create not defined in the body.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            Boolean userExists = userRepository.existsByName(userPostRequest.getName());
            if (userExists) {
                errorResponse.put("message", String.format("User with name %s is already exists.", userPostRequest.getName()));
                errorResponse.put("status", HttpStatus.CONFLICT.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            User user = userRepository
                    .save(new User(userPostRequest.getName(), null));
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/id/{userId}/version/{version}")
    public ResponseEntity<?> updateUser(@PathVariable(required = true) Long userId,
                                        @PathVariable(required = true) Long version,
                                        @RequestBody User userPutRequest) {
        Map<String, String> errorResponse = new HashMap<>();
        if (userId == null) {
            errorResponse.put("message", "User id must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if (version == null) {
            errorResponse.put("message", "Version must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if (userPutRequest == null) {
            errorResponse.put("message", "User to create not defined in the body.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<User> userFoundById = userRepository.findById(userId);
            if (!userFoundById.isPresent()) {
                errorResponse.put("message", String.format("User with id %d not found.", userId));
                errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            if (!userFoundById.get().getVersion().equals(version)) {
                errorResponse.put("message", String.format("Specified version(%d) doesn't match the current one (%d).",
                        version, userFoundById.get().getVersion()));
                errorResponse.put("status", HttpStatus.CONFLICT.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            User updatedUser = userFoundById.get();
            updatedUser.setId(userFoundById.get().getId());
            updatedUser.setName(userPutRequest.getName());
            updatedUser.setVersion(userPutRequest.getVersion());
            User user = userRepository.save(updatedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/id/{userId}/version/{version}")
    public ResponseEntity<?> deleteUser(@PathVariable(required = true) Long userId,
                                        @PathVariable(required = true) Long version) {
        Map<String, String> errorResponse = new HashMap<>();
        if (userId == null) {
            errorResponse.put("message", "User id must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if (version == null) {
            errorResponse.put("message", "Version must be specified.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        try {
            Optional<User> userExists = userRepository.findById(userId);
            if (!userExists.isPresent()) {
                errorResponse.put("message", String.format("User with id %d not found.", userId));
                errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            if (!userExists.get().getVersion().equals(version)) {
                errorResponse.put("message", String.format("Specified version(%d) doesn't match the current one (%d).",
                        version, userExists.get().getVersion()));
                errorResponse.put("status", HttpStatus.CONFLICT.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
            List<UserRole> userRoleExists = userRoleRepository.findAllByUserId(userId);
            if (!userRoleExists.isEmpty()) {
                errorResponse.put("message", String.format("There are user roles for user with id = (%d).",userId));
                errorResponse.put("status", HttpStatus.CONFLICT.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
           userRepository.delete(userExists.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
