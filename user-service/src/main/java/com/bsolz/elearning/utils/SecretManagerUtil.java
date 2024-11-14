package com.bsolz.elearning.utils;

import com.bsolz.elearning.bo.AppSecrets;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class SecretManagerUtil {

    private final SecretsManagerClient secretsManagerClient;

    private final String secretId;

    private final ObjectMapper mapper;

    public SecretManagerUtil(
            final SecretsManagerClient secretsManagerClient,
            @Value("${spring.aws.secret.id}") String secretId,
            final ObjectMapper mapper
    ) {
        this.mapper = mapper;
        this.secretsManagerClient = secretsManagerClient;
        this.secretId = secretId;
    }

    public AppSecrets getAppSecrets() {
        try {
            GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                    .secretId(secretId)
                    .build();

            GetSecretValueResponse valueResponse = secretsManagerClient.getSecretValue(valueRequest);
            String secret = valueResponse.secretString();
            return mapper.readValue(secret, AppSecrets.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String MAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                MAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(MAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
