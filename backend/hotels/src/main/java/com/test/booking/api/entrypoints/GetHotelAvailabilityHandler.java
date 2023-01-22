package com.test.booking.api.entrypoints;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.test.booking.commons.config.db.DBConnectionService;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.common.ApiException;
import com.test.booking.commons.service.CommonReservationService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
public class GetHotelAvailabilityHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Connection connection = DBConnectionService.getDBConnection();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            String roomId = event.getPathParameters().get("room_id");
            log.info("Getting availability for room <{}>", roomId);
            List<LocalDate> availableDates = CommonReservationService.getAvailabilityByRoomId(connection, UUID.fromString(roomId));
            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(MapperConfig.getObjectMapper().writeValueAsString(availableDates))
                    .build();
        }
        catch (ApiException e) {
            log.error("API Exception encountered: <{}>", e.getBody().toString());
            return APIGatewayV2HTTPResponse.builder().withStatusCode(e.getStatus()).withBody(e.getBody().toString()).build();
        }
        catch (Exception e) {
            log.error("Unexpected exception encountered: <{}>. Stack Trace: <{}>", e.getMessage(), e.getStackTrace());
            return APIGatewayV2HTTPResponse.builder().withStatusCode(500).withBody("Unexpected Exception").build();
        }
    }
}