package com.test.booking.commons.util.identity;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class IdentityService {

    public static final IdentityType IDENTITY_TYPE = IdentityType.valueOf(System.getenv().get("IDENTITY_TYPE"));

    public static Identity getIdentity(Map<String, Object> event) {
        Map<String, String> context = (Map<String, String>) event.get("context");
        Identity identity = Identity.builder()
                .userId(context.get("userId"))
                .build();
        log.info("User's identity calling API: <{}>", identity);
        return identity;
    }
}
