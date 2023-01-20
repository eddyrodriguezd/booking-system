package com.test.booking.commons.util.identity;

import com.test.booking.commons.exception.InvalidIdentityException;

public class IdentityService {

    private static final String ADMIN_IDENTITY_POOL_ID = System.getenv().get("ADMIN_IDENTITY_POOL_ID");
    private static final String USER_IDENTITY_POOL_ID = System.getenv().get("USER_IDENTITY_POOL_ID");

    public static IdentityType getIdentityType(String identityPoolId) {
        if(identityPoolId.equals(ADMIN_IDENTITY_POOL_ID)) return IdentityType.ADMIN;
        if(identityPoolId.equals(USER_IDENTITY_POOL_ID)) return IdentityType.USER;
        throw new InvalidIdentityException(identityPoolId);
    }
}
