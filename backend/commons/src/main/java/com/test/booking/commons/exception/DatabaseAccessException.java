package com.test.booking.commons.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

public class DatabaseAccessException extends ApiException {

    public DatabaseAccessException(String query) {
        super("002", HttpStatusCode.INTERNAL_SERVER_ERROR, "SQL query <" + query + "> failed");
    }
}
