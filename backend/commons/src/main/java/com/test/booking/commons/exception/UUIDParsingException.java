package com.test.booking.commons.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

public class UUIDParsingException extends ApiException {

    public UUIDParsingException(String str) {
        super("003", HttpStatusCode.INTERNAL_SERVER_ERROR, "UUID <" + str + "> couldn't be set as a Postgres Object with type 'uuid'");
    }
}
