package com.test.booking.commons.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

public class DatabaseConnectionException extends ApiException {

    public DatabaseConnectionException(String url) {
        super("001", HttpStatusCode.INTERNAL_SERVER_ERROR, "Connection to database at <" + url + "> failed");
    }
}
