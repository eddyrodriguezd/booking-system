package com.test.booking.commons.config.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.commons.config.mapper.MapperConfig;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Slf4j
public class SecretsManagerService {
    private static final String VERSION_STAGE = "AWSCURRENT";
    private static final String RDS_SECRET_ARN = System.getenv().get("RDS_SECRET_ARN");

    private static final SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build();

    public static DBCredentials getDatabaseCredentials() throws JsonProcessingException {
        String secret = getSecret();
        log.info("Secret: " + secret);
        return MapperConfig.getObjectMapper().readValue(secret, DBCredentials.class);
    }

    private static String getSecret() {
        log.info("Retrieving secret from <{}>", RDS_SECRET_ARN);
        return secretsManagerClient.getSecretValue(
                GetSecretValueRequest.builder()
                        .secretId(RDS_SECRET_ARN)
                        .versionStage(VERSION_STAGE)
                        .build()
        ).secretString();
    }
}