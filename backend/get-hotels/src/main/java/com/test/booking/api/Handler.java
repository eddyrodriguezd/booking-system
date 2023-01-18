package com.test.booking.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.amazonaws.services.lambda.runtime.serialization.util.ReflectUtil;
import com.test.booking.api.model.Hotel;

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

    @Override
    public String handleRequest(Object event, Context context) {
        LambdaLogger lambdaLogger = context.getLogger();
        lambdaLogger.log("Test");
        Hotel hotel = new Hotel(UUID.randomUUID(), "Hotel 1", "CA", -12.54, 45.56);

        return String.valueOf(hotel);
    }

}