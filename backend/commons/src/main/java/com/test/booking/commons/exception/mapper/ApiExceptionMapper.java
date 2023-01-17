package com.test.booking.commons.exception.mapper;

import com.test.booking.commons.exception.ApiException;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    @Override
    public Response toResponse(ApiException exception) {
        return Response.status(exception.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity(exception.getBody())
                .build();
    }
}
