package com.bsolz.elearning.controllers;

import com.bsolz.elearning.models.requests.CognitoUserRequest;
import com.bsolz.elearning.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody CognitoUserRequest registrationRequest) {
        final String register = userService.register(registrationRequest);
        return ResponseEntity.ok(register);
    }

    @PostMapping("/signup/confirm/{code}")
    public ResponseEntity<String> confirmUser(
            @PathVariable String code,
            @RequestParam String username
    ) {
        final String confirmSignUpResponse = userService.confirmSignUp(username, code);
        return ResponseEntity.ok(confirmSignUpResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<HashMap<String, String>> login(
            @RequestBody CognitoUserRequest loginRequest
    ) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser() {
        userService.updateUserDetails();
        return ResponseEntity.ok("Authenticated");
    }
}
