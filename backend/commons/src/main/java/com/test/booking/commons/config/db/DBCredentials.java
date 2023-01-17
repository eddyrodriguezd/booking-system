package com.test.booking.commons.config.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@RegisterForReflection
public class DBCredentials {
    private String username;
    private String password;
    private String host;
}
