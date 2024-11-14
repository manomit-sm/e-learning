package com.bsolz.elearning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

import java.util.UUID;

@Configuration
public class UserAppConfig {

    private final String region;
    private final String roleArn;

    public UserAppConfig(
            @Value("${spring.aws.region}") String region,
            @Value("${spring.aws.iam.role.arn}") String roleArn
    ) {
        this.region = region;
        this.roleArn = roleArn;
    }

    @Bean
    public AwsCredentialsProvider awsCredentialsProvider() {
        StsClient stsClient = StsClient.builder()
                .region(Region.of(region))
                .build();
        AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                .roleArn(roleArn)
                .roleSessionName(UUID.randomUUID().toString())
                .build();
        return StsAssumeRoleCredentialsProvider.builder().refreshRequest(roleRequest).stsClient(stsClient).build();
    }

    @Bean
    public SecretsManagerClient awsSecretsManager() {
        return SecretsManagerClient.builder()
                .credentialsProvider(awsCredentialsProvider())
                .region(Region.of(region))
                .build();
    }

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(awsCredentialsProvider())
                .region(Region.of(region))
                .build();
    }
}
