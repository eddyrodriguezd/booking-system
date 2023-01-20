package com.test.booking.api.entrypoints;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.test.booking.api.service.ReservationAdminService;
import com.test.booking.api.service.ReservationService;
import com.test.booking.commons.config.db.DBConnectionService;
import com.test.booking.commons.exception.common.ApiException;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.util.identity.IdentityService;
import com.test.booking.commons.util.identity.IdentityType;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

@Slf4j
public class GetReservationsHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Connection connection = DBConnectionService.getDBConnection();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            IdentityType identityType = IdentityService.getIdentityType(context.getIdentity().getIdentityPoolId());
            log.info("Getting reservations as <{}>", identityType.name());

            List<Reservation> reservations = null;
            switch (identityType) {
                case ADMIN:
                    reservations = ReservationAdminService.getReservations(connection);
                    break;
                case USER:
                    String guestId = event.getPathParameters().get("guest_id");
                    reservations = ReservationService.getReservations(connection, UUID.fromString(guestId));
            }

            return APIGatewayV2HTTPResponse.builder().withStatusCode(200).withBody(reservations.toString()).build();
        }
        catch (ApiException e) {
            log.error("API Exception encountered: <{}>", e.getBody().toString());
            return APIGatewayV2HTTPResponse.builder().withStatusCode(e.getStatus()).withBody(e.getBody().toString()).build();
        }
        catch (Exception e) {
            log.error("Unexpected exception encountered: <{}>", e.getMessage());
            return APIGatewayV2HTTPResponse.builder().withStatusCode(500).withBody("Unexpected Exception").build();
        }
    }
}