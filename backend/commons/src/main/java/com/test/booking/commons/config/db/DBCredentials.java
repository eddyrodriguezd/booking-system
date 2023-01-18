package com.test.booking.commons.config.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class DBCredentials {
    private String username;
    private String password;
    private String host;
}
