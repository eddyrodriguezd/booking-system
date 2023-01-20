package com.test.booking.commons.exception;

import com.test.booking.commons.exception.common.ApiException;
import software.amazon.awssdk.http.HttpStatusCode;

public class InvalidIdentityException extends ApiException {

    public InvalidIdentityException(String identityPoolId) {
        super("005", HttpStatusCode.BAD_REQUEST, "Identity Pool Id <{" + identityPoolId + "}> is invalid");
    }
}
