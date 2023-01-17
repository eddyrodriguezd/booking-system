package com.test.booking.commons.config.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.commons.config.mapper.MapperConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Slf4j
public class SecretsManagerService {
    private static final String VERSION_STAGE = "AWSCURRENT";

    @ConfigProperty(name = "db.secret.arn")
    String secretArn;

    private final SecretsManagerClient secretsManagerClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(DefaultCredentialsProvider.create())
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build();

    public DBCredentials getDatabaseCredentials() throws JsonProcessingException {
        return MapperConfig.getObjectMapper().readValue(getSecret(), DBCredentials.class);
    }

    private String getSecret() {
        log.info("Retrieving secret from <{}>", secretArn);
        return secretsManagerClient.getSecretValue(
                GetSecretValueRequest.builder()
                        .secretId(secretArn)
                        .versionStage(VERSION_STAGE)
                        .build()
        ).secretString();
    }
}
