package com.nimfid.commons.util;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {
    private final static ZoneId ZONE_ID = ZoneId.of("Africa/Lagos");

    public static Date getDateOfInstant() {
        Instant now = Instant.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, ZONE_ID);
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    public static ZonedDateTime getZonedDateTimeOfInstant() {
        Instant now = Instant.now();
        return ZonedDateTime.ofInstant(now, ZONE_ID);
    }

    public static String getFormattedDateTimeOfInstant() {
        Instant now = Instant.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, ZONE_ID);
        return DateTimeFormatter.ofPattern("EEE, dd MMM yyyy 'at' hh:mm:ss a").format(zonedDateTime);
    }

    public static ZoneId getZONE_ID() {
        return ZONE_ID;
    }

}
