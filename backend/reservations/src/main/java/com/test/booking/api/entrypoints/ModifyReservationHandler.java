package com.test.booking.api.entrypoints;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.test.booking.api.dto.ReservationDto;
import com.test.booking.api.repository.ReservationRepository;
import com.test.booking.api.repository.ReservationRepositoryImpl;
import com.test.booking.api.service.ReservationService;
import com.test.booking.commons.config.db.DBConnectionService;
import com.test.booking.commons.config.mapper.MapperConfig;
import com.test.booking.commons.exception.common.ApiException;
import com.test.booking.commons.model.Reservation;
import com.test.booking.commons.util.identity.Identity;
import com.test.booking.commons.util.identity.IdentityService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ModifyReservationHandler implements RequestHandler<Map<String, Object>, APIGatewayV2HTTPResponse> {

    private static final Connection connection = DBConnectionService.getDBConnection();
    private static final ReservationRepository reservationRepository = new ReservationRepositoryImpl();
    private static final ReservationService reservationService = new ReservationService(connection, reservationRepository);

    @Override
    public APIGatewayV2HTTPResponse handleRequest(Map<String, Object> event, Context context) {
        try {
            log.info("ModifyReservationHandler::handleRequest. Event received: <{}>", MapperConfig.getObjectMapper().writeValueAsString(event));
            Identity identity = IdentityService.getIdentity(event);
            log.info("Modifying reservation for user=<{}>", identity.getUserId());

            Map<String, Object> parameters = (Map<String, Object>) event.get("params");
            Map<String, String> pathParams = (Map<String, String>) parameters.get("path");
            String reservationId = pathParams.get("reservation_id");
            log.info("Reservation with id =<{}> will be updated", reservationId);

            Map<String, String> body = (Map<String, String>) event.get("body");
            log.info("Body received from API Gateway: <{}>", body);
            ReservationDto reservationDto = new ReservationDto(body);
            log.info("Reservation received from API Gateway: <{}>", reservationDto);

            Reservation reservationUpdated = reservationService.modifyReservation(UUID.fromString(reservationId), UUID.fromString(identity.getUserId()), reservationDto);

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