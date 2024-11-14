package com.bsolz.elearning.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public record CognitoUserRequest(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("name") String name,
        @JsonProperty("address") String address,
        @JsonProperty("phoneNumber") String phoneNumber

        ) {
    public CognitoUserRequest {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        if (username.isBlank() && password.isBlank())
            throw new RuntimeException("Email or Password can not be empty");
    }
}
