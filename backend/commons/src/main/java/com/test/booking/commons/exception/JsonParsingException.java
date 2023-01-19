package com.test.booking.commons.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

public class JsonParsingException extends ApiException {

    public JsonParsingException(String str) {
        super("003", HttpStatusCode.INTERNAL_SERVER_ERROR, "Object <" + str + "> couldn't be parsed");
    }
}
