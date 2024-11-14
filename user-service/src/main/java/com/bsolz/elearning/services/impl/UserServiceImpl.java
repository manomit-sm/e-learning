package com.bsolz.elearning.services.impl;

import com.bsolz.elearning.bo.AppSecrets;
import com.bsolz.elearning.entities.User;
import com.bsolz.elearning.repositories.UserRepository;
import com.bsolz.elearning.utils.SecretManagerUtil;
import com.bsolz.elearning.models.requests.CognitoUserRequest;
import com.bsolz.elearning.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final CognitoIdentityProviderClient client;

    private final SecretManagerUtil secretManagerUtil;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public String register(CognitoUserRequest registrationRequest) {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            final AppSecrets appSecrets = secretManagerUtil.getAppSecrets();
            CompletableFuture.supplyAsync(() -> {
                        SignUpRequest request = SignUpRequest
                                .builder()
                                .clientId(appSecrets.cognitoClientId())
                                .secretHash(secretManagerUtil.calculateSecretHash(appSecrets.cognitoClientId(), appSecrets.cognitoClientSecret(), registrationRequest.username()))
                                .username(registrationRequest.username())
                                .password(registrationRequest.password())
                                .build();
                        return client.signUp(request);
                    }, executorService)
                    .thenApplyAsync(cognitoUserDetails -> {
                        User user = new User();
                        user.setCognitoUserId(cognitoUserDetails.userSub());
                        user.setEmail(registrationRequest.username());
                        user.setPhoneNo(registrationRequest.phoneNumber());
                        user.setStatus(false);
                        return userRepository.save(user);
                    }, executorService).join();
            return "Successfully registered. Please check your email for verification";
        } catch (UsernameExistsException ex) {
           throw  UsernameExistsException
                   .builder()
                   .message(ex.getMessage())
                   .cause(ex)
                   .statusCode(400)
                   .build();
        }
    }

    @Override
    public String confirmSignUp(String username, String confirmationCode) {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            final AppSecrets appSecrets = secretManagerUtil.getAppSecrets();
            CompletableFuture.supplyAsync(() -> {
                        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest
                                .builder()
                                .clientId(appSecrets.cognitoClientId())
                                .secretHash(secretManagerUtil.calculateSecretHash(appSecrets.cognitoClientId(), appSecrets.cognitoClientSecret(), username))
                                .username(username)
                                .confirmationCode(confirmationCode)
                                .build();
                        return client.confirmSignUp(confirmSignUpRequest);
                    }, executorService)
                    .thenAccept(confirmSignUpResponse -> {
                        final User user = userRepository.findByEmail(username);
                        user.setStatus(true);
                        userRepository.save(user);
                    }).join();
            return "Email successfully verified";
        } catch (ExpiredCodeException ex) {
            throw  ExpiredCodeException
                    .builder()
                    .message(ex.getMessage())
                    .cause(ex)
                    .statusCode(400)
                    .build();
        }
    }

    @Override
    public HashMap<String, String> login(CognitoUserRequest loginRequest) {
        try {
            final AppSecrets appSecrets = secretManagerUtil.getAppSecrets();
            Map<String, String> authParams = new LinkedHashMap<>() {{
                put("USERNAME", loginRequest.username());
                put("PASSWORD", loginRequest.password());
                put("SECRET_HASH", secretManagerUtil.calculateSecretHash(appSecrets.cognitoClientId(), appSecrets.cognitoClientSecret(), loginRequest.username()));
            }};


            AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .userPoolId(appSecrets.cognitoUserPoolId())
                    .clientId(appSecrets.cognitoClientId())
                    .authParameters(authParams).build();
            final AdminInitiateAuthResponse authResult = client.adminInitiateAuth(authRequest);
            AuthenticationResultType resultType = authResult.authenticationResult();
            return new LinkedHashMap<>() {{
                put("idToken", resultType.idToken());
                put("accessToken", resultType.accessToken());
                put("refreshToken", resultType.refreshToken());
                put("message", "Successfully login");
            }};
        } catch (NotAuthorizedException ex) {
            throw NotAuthorizedException.builder()
                    .message(ex.getMessage())
                    .cause(ex)
                    .statusCode(400)
                    .build();
        }
    }

    @Override
    public void updateUserDetails() {
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

    }
}
