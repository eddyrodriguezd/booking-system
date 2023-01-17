package com.test.booking.commons.config.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.commons.exception.ApiException;
import io.quarkus.arc.Unremovable;
import io.quarkus.credentials.CredentialsProvider;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
@Unremovable
@Named("db-credentials-provider")
@Slf4j
public class DBCredentialsProvider implements CredentialsProvider {
    @Inject
    SecretsManagerService secretsManagerService;

    @Override
    public Map<String, String> getCredentials(String credentialsProviderName) {
        try {
            DBCredentials dbCredentials = secretsManagerService.getDatabaseCredentials();
            Map<String, String> properties = new HashMap<>();
            properties.put(USER_PROPERTY_NAME, dbCredentials.getUsername());
            properties.put(PASSWORD_PROPERTY_NAME, dbCredentials.getPassword());
            return properties;
        } catch (JsonProcessingException e) {
            throw new ApiException("002", 409, "DB Credentials JSON couldn't be parsed");
        }
    }

}
