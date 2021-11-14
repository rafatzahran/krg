package com.example.krg.controllers;

import com.example.krg.models.UserRole;
import com.example.krg.repository.RoleRepository;
import com.example.krg.repository.UnitRepository;
import com.example.krg.repository.UserRepository;
import com.example.krg.repository.UserRoleRepository;
import com.example.krg.util.Utility;
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
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> getAllUserRolesGivenUserIdAndUnitId(@PathVariable(required = true) Long userId,
                                                                           @PathVariable(required = true) Long unitId) {

        if (userId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "UserId must be specified.");
        }
        if (unitId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "UnitId must be specified.");
        }

        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                    String.format("User with id = %s not found.", userId));
        }

        boolean unitExists = unitRepository.existsById(unitId);
        if (!unitExists) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                    String.format("Unit with id = %s not found.", unitId));
        }

        try {
            List<UserRole> userRoles = userRoleRepository.findByUserIdAndUnitId(userId, unitId);

            if (userRoles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userRoles, HttpStatus.OK);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get all users roles for given user id and  unit id: %s", e.getMessage()));
        }
    }

    @GetMapping("/valid/userid/{userId}/unitid/{unitId}/dateTime/{dateTime}")
    public ResponseEntity<?> getValidUserRolesGivenUserIdUnitIdAndDateTime(@PathVariable(required = true) Long userId,
                                                                                    @PathVariable(required = true) Long unitId,
                                                                                    @PathVariable(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateTime) {

        if (userId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "UserId must be specified.");
        }
        if (unitId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "UnitId must be specified.");
        }
        if (dateTime == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Datetime must be specified.");
        }

        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                    String.format("User with id = %s not found.", userId));
        }

        boolean unitExists = unitRepository.existsById(unitId);
        if (!unitExists) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                    String.format("Unit with id = %s not found.", unitId));
        }

        try {
            List<UserRole> userRoles = userRoleRepository.findOnlyValidGivenDateTimeUserIdAndUnitId(userId, unitId, dateTime);
            return new ResponseEntity<>(userRoles, HttpStatus.OK);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get valid user roles given user id, unit id and datetime. %s", e.getMessage()));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createUser(@RequestBody UserRole userRolePostRequest) {

        if (userRolePostRequest == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User role to create not defined in the body.");
        }

        Long userId = userRolePostRequest.getUserId();
        if (userId == null || userId < 1) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User id defined in the body not valid.");
        }

        Long unitId = userRolePostRequest.getUnitId();
        if (unitId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Unit id defined in the body not valid.");
        }

        Long roleId = userRolePostRequest.getRoleId();
        if (roleId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Role id defined in the body not valid.");
        }

        LocalDateTime newValidFrom = Optional.ofNullable(userRolePostRequest.getValidFrom()).orElse(LocalDateTime.now());
        Optional<LocalDateTime> newValidTo = Optional.ofNullable(userRolePostRequest.getValidTo());
        if (newValidTo.isPresent() && newValidTo.get().isBefore(newValidFrom)) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Specified valid to timestamp must be after the valid from timestamp.");
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
                    return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                            String.format("User role with id %d has validation range that overlap the validation " +
                                            "range of the user role to create. At most one user role for a given combination of user id," +
                                            " unit id and role id can be valid at any point in time.",
                                    userRoleOverlappingWithTheNewOne.get().getId()));
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
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to create new user role. %s", e.getMessage()));
        }
    }

    @PutMapping("/id/{userRoleId}/version/{version}")
    public ResponseEntity<?> updateUserRole(@PathVariable(required = true) Long userRoleId,
                                            @PathVariable(required = true) Long version,
                                            @RequestBody UserRole userRolePutRequest) {

        if (userRolePutRequest == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User role to update not defined in the body.");
        }

        if (userRoleId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User role id can not be null.");
        }

        if (version == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Version can not be null.");
        }

        LocalDateTime newValidFrom = Optional.ofNullable(userRolePutRequest.getValidFrom()).orElse(LocalDateTime.now());
        Optional<LocalDateTime> newValidTo = Optional.ofNullable(userRolePutRequest.getValidTo());
        if (newValidTo.isPresent() && newValidTo.get().isBefore(newValidFrom)) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Specified valid to timestamp must be after the valid from timestamp.");
        }

        try {
            Optional<UserRole> userRoleFoundById = userRoleRepository.findById(userRoleId);
            if (!userRoleFoundById.isPresent()) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                        String.format("User role with id %d not found.", userRoleId));
            }
            if (!userRoleFoundById.get().getVersion().equals(version)) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                        String.format("Specified version(%d) doesn't match the current one (%d).",
                                version, userRoleFoundById.get().getVersion()));
            }

            Long userId = userRolePutRequest.getUserId();
            Long unitId = userRolePutRequest.getUnitId();
            Long roleId = userRolePutRequest.getRoleId();

            UserRole userRoleToUpdate = buildUserRoleToUpdate(userRolePutRequest, newValidFrom, newValidTo,
                    userRoleFoundById, userId, unitId, roleId);

            List<UserRole> userRoleListFoundByIds = userRoleRepository.findByUserIdUnitIdAndRoleId(userId, unitId, roleId);
            if (!userRoleListFoundByIds.isEmpty()) {
                Optional<UserRole> userRoleOverlappingWithTheNewOne = findUserRoleValidWithinRange(userRoleListFoundByIds,
                        userRoleId, newValidFrom, newValidTo);
                if (userRoleOverlappingWithTheNewOne.isPresent()) {
                    return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                            String.format("User role with id %d has validation range that overlap the validation " +
                                            "range of the user role with new info to update. At most one user role for a given combination of user id," +
                                            " unit id and role id can be valid at any point in time.",
                                    userRoleOverlappingWithTheNewOne.get().getId()));
                }
            }

            UserRole userRole = userRoleRepository.save(userRoleToUpdate);
            return new ResponseEntity<>(userRole, HttpStatus.CREATED);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to update user role. %s", e.getMessage()));
        }
    }

    private UserRole buildUserRoleToUpdate(@RequestBody UserRole userRolePutRequest, LocalDateTime newValidFrom,
                                           Optional<LocalDateTime> newValidTo, Optional<UserRole> userRoleFoundById,
                                           Long userId, Long unitId, Long roleId) {
        UserRole userRoleToUpdate = userRoleFoundById.get();
        userRoleToUpdate.setId(userRoleFoundById.get().getId());
        userRoleToUpdate.setVersion(userRolePutRequest.getVersion());
        userRoleToUpdate.setUserId(userId);
        userRoleToUpdate.setUnitId(unitId);
        userRoleToUpdate.setRoleId(roleId);
        userRoleToUpdate.setValidFrom(newValidFrom);
        userRoleToUpdate.setValidTo(newValidTo.orElse(null));
        return userRoleToUpdate;
    }

    private Optional<UserRole> findUserRoleValidWithinRange(List<UserRole> userRoleListFoundByIds, Long userRoleId, LocalDateTime newValidFrom,
                                                            Optional<LocalDateTime> newValidTo) {
        return userRoleListFoundByIds.stream()
                .filter(existUserRole -> !userRoleId.equals(existUserRole.getId()))//Skip the actual user role
                .filter(existUserRole -> {
                    LocalDateTime existValidToToUse = Optional.ofNullable(existUserRole.getValidTo()).orElse(LocalDateTime.MAX);
                    LocalDateTime existValidFrom = existUserRole.getValidFrom();
                    LocalDateTime newValidToToUse = newValidTo.orElse(LocalDateTime.MAX);
                    return newValidFrom.isBefore(existValidToToUse) && newValidToToUse.isAfter(existValidFrom);
                })
                .findFirst();
    }

    @DeleteMapping("/id/{userRoleId}/version/{version}")
    public ResponseEntity<?> deleteUserRole(@PathVariable(required = true) Long userRoleId,
                                            @PathVariable(required = true) Long version) {

        if (userRoleId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User role id must be specified.");
        }
        if (version == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Version must be specified.");
        }
        try {
            Optional<UserRole> userRoleFoundById = userRoleRepository.findById(userRoleId);
            if (!userRoleFoundById.isPresent()) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                        String.format("User role with id %d not found.", userRoleId));
            }
            if (!userRoleFoundById.get().getVersion().equals(version)) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                        String.format("Specified version(%d) doesn't match the current one (%d).",
                                version, userRoleFoundById.get().getVersion()));
            }

            userRoleRepository.delete(userRoleFoundById.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to delete user role: %s", e.getMessage()));
        }
    }
}
