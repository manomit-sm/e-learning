package com.bsolz.elearning.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppSecrets(
        @JsonProperty("COGNITO_CLIENT_ID") String cognitoClientId,
        @JsonProperty("COGNITO_USER_POOL_ID") String cognitoUserPoolId,
        @JsonProperty("COGNITO_CLIENT_SECRET") String cognitoClientSecret
) {
}
