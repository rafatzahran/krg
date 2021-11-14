package com.example.krg.util;

import com.example.krg.models.User;
import com.example.krg.models.UserDTO;

public final class Utility {

    public static UserDTO mapUserToDTO(final User user) {
        return new UserDTO().buildFromUserEntity(user);
    }
}
