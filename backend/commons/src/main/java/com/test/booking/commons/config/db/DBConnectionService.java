package com.test.booking.commons.config.db;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class DBConnectionService {
    private static final String RDS_SECRET_ARN = System.getenv().get("RDS_SECRET_ARN");

    public static Connection getDBConnection() {
        try {
            Class.forName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver").getDeclaredConstructor().newInstance();

            Properties info = new Properties();
            info.put("user", RDS_SECRET_ARN);

            Connection connection = DriverManager.getConnection(RDS_SECRET_ARN, info);
            log.info("Successfully connected to database in <{}>.", RDS_SECRET_ARN);
            return connection;
        }
        catch (SQLException e) {
            log.error("Connection couldn't be established to database in <{}>. Error: <{}>. Stack Trace: <{}>.", RDS_SECRET_ARN, e.getMessage(), e.getStackTrace());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("Class <com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver> couldn't be loaded. Error: <{}>. Stack Trace: <{}>.", e.getMessage(), e.getStackTrace());
        }
        return null;
    }

    public static boolean isConnectionClosed(Connection connection) {
        try {
            if (connection.isClosed()) {
                log.info("Connection has been closed. Reconnecting.");
            }
            return connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

}
