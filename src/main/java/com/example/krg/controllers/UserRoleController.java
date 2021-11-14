package com.example.krg.controllers;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    RoleRepository roleRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @GetMapping("/all/userid/{userId}/unitid/{unitId}")
    public ResponseEntity<Object> getAllUserRolessGivenUserIdAndUnitId(@PathVariable(required = true) Long userId,
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

    @GetMapping("/valid/userid/{userId}/unitid/{unitId}/dateTime/{dateTime}")
    public ResponseEntity<?> getValidUserRolesGivenUserIdAndUnitIdAndDateTime(@PathVariable(required = true) Long userId,
                                                                                    @PathVariable(required = true) Long unitId,
                                                                                    @PathVariable(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateTime) {
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
        if (dateTime == null) {
            errorResponse.put("message", "datetime must be specified.");
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
            List<UserRole> userRoles = userRoleRepository.findOnlyValidGivenDateTimeUserIdAndUnitId(userId, unitId, dateTime);

            if (userRoles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userRoles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createUser(@RequestBody UserRole userRolePostRequest) {
        Map<String, String> errorResponse = new HashMap<>();

        if (userRolePostRequest == null) {
            errorResponse.put("message", "User role to create not defined in the body.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Long userId = userRolePostRequest.getUserId();
        if (userId == null || userId < 1) {
            errorResponse.put("message", "User id defined in the body not valid.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Long unitId = userRolePostRequest.getUnitId();
        if (unitId == null) {
            errorResponse.put("message", "Unit id defined in the body not valid.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        Long roleId = userRolePostRequest.getRoleId();
        if (roleId == null) {
            errorResponse.put("message", "Role id defined in the body not valid.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime newValidFrom = Optional.ofNullable(userRolePostRequest.getValidFrom()).orElse(LocalDateTime.now());
        Optional<LocalDateTime> newValidTo = Optional.ofNullable(userRolePostRequest.getValidTo());
        if (newValidTo.isPresent() && newValidTo.get().isBefore(newValidFrom)) {
            errorResponse.put("message", "Specified valid to timestamp must be after the valid from timestamp.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }


        try {
            List<UserRole> userRoleListFoundByIds = userRoleRepository.findByUserIdUnitIdAndRoleId(userId, unitId, roleId);
            if (!userRoleListFoundByIds.isEmpty()) {
                Optional<UserRole> userRoleOverlappingWithTheNewOne = userRoleListFoundByIds.stream()
                        .filter(existUserRole -> {
                            LocalDateTime existValidToToUse = Optional.ofNullable(existUserRole.getValidTo()).orElse(LocalDateTime.MAX);
                            LocalDateTime existValidFrom = existUserRole.getValidFrom();
                            LocalDateTime newValidToToUse = newValidTo.orElse(LocalDateTime.MAX);
                            return newValidFrom.isBefore(existValidToToUse) && newValidToToUse.isAfter(existValidFrom);
                        })
                        .findFirst();
                if (userRoleOverlappingWithTheNewOne.isPresent()) {
                    errorResponse.put("message", String.format("User role with id %d has validation range that overlap the validation " +
                            "range of the user role to create. At most one user role for a given combination of user id," +
                            " unit id and role id can be valid at any point in time.",
                            userRoleOverlappingWithTheNewOne.get().getId()));
                    errorResponse.put("status", HttpStatus.CONFLICT.toString());
                    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
                }
            }

            UserRole userRole = userRoleRepository
                    .save(new UserRole(userRolePostRequest.getVersion(),
                            userRolePostRequest.getUserId(),
                            userRolePostRequest.getUnitId(),
                            userRolePostRequest.getRoleId(),
                            userRolePostRequest.getValidFrom(),
                            userRolePostRequest.getValidTo()
                            ));
            return new ResponseEntity<>(userRole, HttpStatus.CREATED);
        } catch (Exception e) {
            errorResponse.put("message", String.format("Failed to create new user role. %s", e.getMessage()));
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/id/{userRoleId}/version/{version}")
    public ResponseEntity<?> updateUserRole(@PathVariable(required = true) Long userRoleId,
                                            @PathVariable(required = true) Long version,
                                            @RequestBody UserRole userRolePutRequest) {
        Map<String, String> errorResponse = new HashMap<>();

        if (userRolePutRequest == null) {
            errorResponse.put("message", "User role to update not defined in the body.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (userRoleId == null) {
            errorResponse.put("message", "User role id can not be null.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (version == null) {
            errorResponse.put("message", "Version can not be null.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        LocalDateTime newValidFrom = Optional.ofNullable(userRolePutRequest.getValidFrom()).orElse(LocalDateTime.now());
        Optional<LocalDateTime> newValidTo = Optional.ofNullable(userRolePutRequest.getValidTo());
        if (newValidTo.isPresent() && newValidTo.get().isBefore(newValidFrom)) {
            errorResponse.put("message", "Specified valid to timestamp must be after the valid from timestamp.");
            errorResponse.put("status", HttpStatus.BAD_REQUEST.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        try {
            Optional<UserRole> userRoleFoundById = userRoleRepository.findById(userRoleId);
            if (!userRoleFoundById.isPresent()) {
                errorResponse.put("message", String.format("User role with id %d not found.", userRoleId));
                errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
            if (!userRoleFoundById.get().getVersion().equals(version)) {
                errorResponse.put("message", String.format("Specified version(%d) doesn't match the current one (%d).",
                        version, userRoleFoundById.get().getVersion()));
                errorResponse.put("status", HttpStatus.CONFLICT.toString());
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }

            Long userId = userRolePutRequest.getUserId();
            Long unitId = userRolePutRequest.getUnitId();
            Long roleId = userRolePutRequest.getRoleId();

            UserRole updatedUser = userRoleFoundById.get();
            updatedUser.setId(userRoleFoundById.get().getId());
            updatedUser.setVersion(userRolePutRequest.getVersion());
            updatedUser.setUserId(userId);
            updatedUser.setUnitId(unitId);
            updatedUser.setRoleId(roleId);
            updatedUser.setValidFrom(newValidFrom);
            updatedUser.setValidTo(newValidTo.orElse(null));

            List<UserRole> userRoleListFoundByIds = userRoleRepository.findByUserIdUnitIdAndRoleId(userId, unitId, roleId);
            if (!userRoleListFoundByIds.isEmpty()) {
                Optional<UserRole> userRoleOverlappingWithTheNewOne = userRoleListFoundByIds.stream()
                        .filter(existUserRole -> !userRoleId.equals(existUserRole.getId()))
                        .filter(existUserRole -> {
                            LocalDateTime existValidToToUse = Optional.ofNullable(existUserRole.getValidTo()).orElse(LocalDateTime.MAX);
                            LocalDateTime existValidFrom = existUserRole.getValidFrom();
                            LocalDateTime newValidToToUse = newValidTo.orElse(LocalDateTime.MAX);
                            return newValidFrom.isBefore(existValidToToUse) && newValidToToUse.isAfter(existValidFrom);
                        })
                        .findFirst();
                if (userRoleOverlappingWithTheNewOne.isPresent()) {
                    errorResponse.put("message", String.format("User role with id %d has validation range that overlap the validation " +
                                    "range of the user role with new info to update. At most one user role for a given combination of user id," +
                                    " unit id and role id can be valid at any point in time.",
                            userRoleOverlappingWithTheNewOne.get().getId()));
                    errorResponse.put("status", HttpStatus.CONFLICT.toString());
                    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
                }
            }


            UserRole userRole = userRoleRepository.save(updatedUser);
            return new ResponseEntity<>(userRole, HttpStatus.CREATED);
        } catch (Exception e) {
            errorResponse.put("message", String.format("Failed to create new user role. %s", e.getMessage()));
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
