package com.test.booking.commons.config.db;

import com.test.booking.commons.exception.DatabaseConnectionException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DBConnectionService {
    private static final String RDS_SECRET_ARN = System.getenv().get("RDS_SECRET_ARN");

    public static Connection getDBConnection() {
        DBCredentials dbCredentials = SecretsManagerService.getDatabaseCredentials();

        String url = "jdbc:postgresql://" + dbCredentials.getHost() + ":" + dbCredentials.getPort() + "/" + dbCredentials.getDbname();
        Properties props = new Properties();
        props.setProperty("user", dbCredentials.getUsername());
        props.setProperty("password", dbCredentials.getPassword());
        try {
            return DriverManager.getConnection(url, props);
        } catch (SQLException e) {
            log.error("Connection couldn't be established to database: <{}>. Error: <{}>. Stack Trace: <{}>.",  url, e.getMessage(), e.getStackTrace());
            throw new DatabaseConnectionException(url);
        }
    }
}
