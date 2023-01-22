package com.test.booking.api.entrypoints;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.test.booking.api.service.ReservationAdminService;
import com.test.booking.api.service.ReservationService;
import com.test.booking.commons.config.db.DBConnectionService;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.common.ApiException;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.util.identity.Identity;
import com.test.booking.commons.util.identity.IdentityService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class GetReservationsHandler implements RequestHandler<Map<String, Object>, APIGatewayV2HTTPResponse> {

    private static final Connection connection = DBConnectionService.getDBConnection();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(Map<String, Object> event, Context context) {
        try {
            log.info("Called GetReservationsHandler::handleRequest. Event received: <{}>", MapperConfig.getObjectMapper().writeValueAsString(event));
            Identity identity = IdentityService.getIdentity(event);
            log.info("Getting reservations as <{}> for user=<{}>", IdentityService.IDENTITY_TYPE.name(), identity.getUserId());

            List<Reservation> reservations = null;
            switch (IdentityService.IDENTITY_TYPE) {
                case ADMIN:
                    reservations = ReservationAdminService.getReservations(connection);
                    break;
                case USER:
                    String guestId = identity.getUserId();
                    reservations = ReservationService.getReservations(connection, UUID.fromString(guestId));
            }

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withBody(MapperConfig.getObjectMapper().writeValueAsString(reservations))
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