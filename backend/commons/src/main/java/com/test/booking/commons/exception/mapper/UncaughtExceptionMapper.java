package com.test.booking.commons.exception.mapper;

import com.test.booking.commons.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class UncaughtExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        log.error("Unexpected Exception encountered: <{}>", exception.getMessage());
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiException.ResponseBody.builder()
                        .code("001")
                        .message("Unexpected Exception")
                        .build()
                ).build();
        return response;
    }
}
