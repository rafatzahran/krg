package com.example.krg.util;

import com.example.krg.models.User;
import com.example.krg.models.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public final class Utility {

    public static UserDTO mapUserToDTO(final User user) {
        return new UserDTO().buildFromUserEntity(user);
    }

    public static ResponseEntity<?> getResponseEntityWithCustomMsg(HttpStatus httpStatus, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        errorResponse.put("status", httpStatus.toString());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
