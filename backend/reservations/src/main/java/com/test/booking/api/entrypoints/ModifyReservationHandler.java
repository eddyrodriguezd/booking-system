package com.test.booking.api.entrypoints;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.service.ReservationService;
import com.test.booking.commons.config.db.DBConnectionService;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.common.ApiException;
import com.test.booking.commons.model.Reservation;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.UUID;

@Slf4j
public class ModifyReservationHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private static final Connection connection = DBConnectionService.getDBConnection();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            String cognitoUserId = event.getRequestContext().getAuthorizer().getJwt().getClaims().get("sub");
            log.info("Modifying reservation for user=<{}>", cognitoUserId);

            String reservationId = event.getPathParameters().get("reservation_id");
            log.info("Reservation with id =<{}> will be updated", reservationId);

            ReservationDto reservationDto = MapperConfig.getObjectMapper().readValue(event.getBody(), ReservationDto.class);

            Reservation reservationUpdated = ReservationService.modifyReservation(connection, UUID.fromString(reservationId), UUID.fromString(cognitoUserId), reservationDto);

            return APIGatewayV2HTTPResponse.builder().withStatusCode(200).withBody(reservationUpdated.toString()).build();
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