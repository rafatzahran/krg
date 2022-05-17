package com.example.krg.controllers;

import com.example.krg.models.EUnit;
import com.example.krg.models.Unit;
import com.example.krg.models.User;
import com.example.krg.models.UserDTO;
import com.example.krg.models.UserRole;
import com.example.krg.repository.UnitRepository;
import com.example.krg.repository.UserRepository;
import com.example.krg.repository.UserRoleRepository;
import com.example.krg.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.krg.util.Utility.mapUserToDTO;

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
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String username) {
        try {
            List<User> users = new ArrayList<User>();

            if (username == null)
                userRepository.findAll().forEach(users::add);
            else
                userRepository.findByName(username).forEach(users::add);

            if (users.isEmpty()) {
                return new ResponseEntity<>(users, HttpStatus.NO_CONTENT);
            }

            List<UserDTO> userDTOList = users.stream()
                    .map(Utility::mapUserToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get all users: %s", e.getMessage()));
        }
    }

    /**
     * List all users <p>with at least one valid user role at a given unit at a given time</p>
     *
     * @param unit of the user role.
     * @param dateTime user role's validation datetime.
     *
     * @return ResponseEntity with list of {@link UserDTO}
     *
     * @throws 400 if path parameter is null.
     * @throws 404 if specified unit not found in unit table.
     * @throws 500 if failed occur while trying to fetch data.
     */
    @GetMapping("/valid/unit/{unit}/dateTime/{dateTime}")
    public ResponseEntity<?> getAllValidUsersGivenUnitAndDateTime(@PathVariable(required = true) EUnit unit,
                                                                  @PathVariable(required = true) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dateTime) {

        if (unit == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Unit must be specified.");
        }
        if (dateTime == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Datetime must be specified.");
        }

        try {
            Optional<Unit> unitToUse = unitRepository.findByName(unit);

            if (!unitToUse.isPresent()) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                        String.format("Unit %s not found.", unit.name()));
            }
            List<User> users = userRepository.findAllValidUsersGivenUnitAndDateTime(unitToUse.get().getId(), dateTime);
            if (users.isEmpty()) {
                return new ResponseEntity<>(users,HttpStatus.OK);
            }
            List<UserDTO> userDTOList = users.stream()
                    .map(Utility::mapUserToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get all valid users given unit and datetime: %s", e.getMessage()));
        }
    }

    @PostMapping("/new")
    public ResponseEntity<?> createUser(@RequestBody User userPostRequest) {

        if (userPostRequest == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User to create not defined in the body.");
        }
        try {
            Boolean userExists = userRepository.existsByName(userPostRequest.getName());
            if (userExists) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                        String.format("User with name %s is already exists.", userPostRequest.getName()));
            }
            User user = userRepository
                    .save(new User(userPostRequest.getName(), null));
            UserDTO userDTO = mapUserToDTO(user);
            return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to create user: %s", e.getMessage()));
        }
    }

    @PutMapping("/id/{userId}/version/{version}")
    public ResponseEntity<?> updateUser(@PathVariable(required = true) Long userId,
                                        @PathVariable(required = true) Long version,
                                        @RequestBody User userPutRequest) {

        if (userId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User id must be specified.");
        }
        if (version == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Version must be specified.");
        }
        if (userPutRequest == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User to create not defined in the body.");
        }
        try {
            Optional<User> userFoundById = userRepository.findById(userId);
            if (!userFoundById.isPresent()) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                        String.format("User with id %d not found.", userId));
            }
            if (!userFoundById.get().getVersion().equals(version)) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                        String.format("Specified version(%d) doesn't match the current one (%d).",
                                version, userFoundById.get().getVersion()));
            }
            User updatedUser = userFoundById.get();
            updatedUser.setId(userFoundById.get().getId());
            updatedUser.setName(userPutRequest.getName());
            updatedUser.setVersion(userPutRequest.getVersion());
            User user = userRepository.save(updatedUser);
            UserDTO userDTO = mapUserToDTO(user);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to update user with id= %d and version = %d: %s", userId, version, e.getMessage()));
        }
    }

    @DeleteMapping("/id/{userId}/version/{version}")
    public ResponseEntity<?> deleteUser(@PathVariable(required = true) Long userId,
                                        @PathVariable(required = true) Long version) {

        if (userId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "User id must be specified.");
        }
        if (version == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST,
                    "Version must be specified.");
        }
        try {
            Optional<User> userFoundById = userRepository.findById(userId);
            if (!userFoundById.isPresent()) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND,
                        String.format("User with id %d not found.", userId));
            }
            if (!userFoundById.get().getVersion().equals(version)) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                        String.format("Specified version(%d) doesn't match the current one (%d).",
                                version, userFoundById.get().getVersion()));
            }
            List<UserRole> userRoleListFoundById = userRoleRepository.findAllByUserId(userId);
            if (!userRoleListFoundById.isEmpty()) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.CONFLICT,
                        String.format("There are user roles for user with id = (%d).",userId));
            }
           userRepository.delete(userFoundById.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to delete user with id= %d and version = %d: %s", userId, version, e.getMessage()));
        }
    }

    @GetMapping(value = "/all/userroles/unitid/{unitId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<?> getAllUsersWithAtLeastOneRoleGivenUnitId(@PathVariable(required = true) Long unitId,
                                                                      @RequestHeader Map<String, String> headers) {
        if (unitId == null) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.BAD_REQUEST, "Unit id must be specified.");
        }

        try {
            Optional<Unit> unitToUse = unitRepository.findById(unitId);

            if (!unitToUse.isPresent()) {
                return Utility.getResponseEntityWithCustomMsg(HttpStatus.NOT_FOUND, "Specified unit id not found.");
            }
            List<User> users = userRepository.getAllUsersWithAtLeastOneRoleGivenUnitId(unitToUse.get().getId());
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<User> usersFiltered = users.stream()
                    .map(user -> {
                        List<UserRole> userRoleListForUnitId = user.getUserRoleList().stream()
                                .filter(userRole -> userRole.getUnitId().equals(unitId))
                                .collect(Collectors.toList());
                        user.setUserRoleList(userRoleListForUnitId);
                        return user;
                    }).collect(Collectors.toList());

            String acceptType = headers.get("accept");
            MediaType mediaType = acceptType != null && acceptType.equals("application/xml") ?
                    MediaType.APPLICATION_XML : MediaType.APPLICATION_JSON;

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(mediaType)
                    .body(usersFiltered);
        } catch (Exception e) {
            return Utility.getResponseEntityWithCustomMsg(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Failed to get users for given unit id: %s", e.getMessage()));
        }
    }



}
