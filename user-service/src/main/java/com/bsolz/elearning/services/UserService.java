package com.bsolz.elearning.services;

import com.bsolz.elearning.models.requests.CognitoUserRequest;

import java.util.HashMap;

public interface UserService {
    String register(CognitoUserRequest registrationRequest);

    String confirmSignUp(String email, String confirmationCode);

    HashMap<String, String> login(CognitoUserRequest loginRequest);

    void updateUserDetails();
}
