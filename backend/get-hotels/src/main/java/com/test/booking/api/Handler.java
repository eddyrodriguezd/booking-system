package com.test.booking.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.serialization.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.booking.api.model.Hotel;
import com.test.booking.commons.config.db.DBConnectionService;
import com.test.booking.commons.config.db.DBCredentials;
import com.test.booking.commons.config.db.SecretsManagerService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

/*
public class Handler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        LambdaLogger lambdaLogger = context.getLogger();
        lambdaLogger.log("Test");
        Hotel hotel = new Hotel(UUID.randomUUID(), "Hotel 1", "CA", -12.54, 45.56);

        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withBody(String.valueOf(hotel))
                .build();
    }
}
*/

public class Handler implements RequestHandler<Object, String> {

    //Connection connection = DBConnectionService.getDBConnection();

    @Override
    public String handleRequest(Object event, Context context) {

        LambdaLogger lambdaLogger = context.getLogger();
        lambdaLogger.log("Test connection1");
        Connection connection = null;
        try {
            DBCredentials dbCredentials = SecretsManagerService.getDatabaseCredentials();
            lambdaLogger.log("Host: " + dbCredentials.getHost());
            connection = DBConnectionService.getDBConnectionV2(dbCredentials);
        } catch (JsonProcessingException e) {
            lambdaLogger.log("Db credentials couldn't be loaded: " + e.getMessage());
            lambdaLogger.log("Db credentials couldn't be loaded2: " + e.getOriginalMessage());
        }

        if(connection != null) {
            try {
                lambdaLogger.log(connection.getSchema());
            } catch (SQLException e) {
                lambdaLogger.log("connection.getSchema() FAILED");
            }
        }
        else {
            lambdaLogger.log("CONNECTION NULL");
        }

        Hotel hotel = new Hotel(UUID.randomUUID(), "Hotel 1", "CA", -12.54, 45.56);

        return String.valueOf(hotel);
    }

}