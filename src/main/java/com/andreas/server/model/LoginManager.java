package com.andreas.server.model;

import com.andreas.common.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class LoginManager {
    private List<UserDTO> loggedInUsers = new ArrayList<>();

    public void addLoggedInUser(UserDTO user){
        if (!isLoggedIn(user))
            loggedInUsers.add(user);
    }

    public void removeLoggedInUser(UserDTO user){
        loggedInUsers.remove(user);
    }

    public boolean isLoggedIn(UserDTO user){
        return loggedInUsers.contains(user);
    }
}
