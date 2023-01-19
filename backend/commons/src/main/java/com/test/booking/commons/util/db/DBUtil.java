package com.test.booking.commons.util.db;

import com.test.booking.commons.exception.UUIDParsingException;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.UUID;

public class DBUtil {

    public static PGobject buildPostgresUUIDObject(UUID uuid) {
        try {
            PGobject pgUUID = new PGobject();
            pgUUID.setType("uuid");
            pgUUID.setValue(uuid.toString());
            return pgUUID;
        } catch (SQLException e) {
            throw new UUIDParsingException(uuid.toString());
        }
    }
}
