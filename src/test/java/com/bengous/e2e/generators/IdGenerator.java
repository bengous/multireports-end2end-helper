package com.bengous.e2e.generators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdGenerator {

    private static final String PREFIX = "2.25.";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmms");

    public static String generateWithUidVariant() {
        var uuid = UUID.randomUUID();

        var mostSigBits = uuid.getMostSignificantBits();
        var leastSigBits = uuid.getLeastSignificantBits();

        var absMostSigBits = Math.abs(mostSigBits);
        var absLeastSigBits = Math.abs(leastSigBits);

        var studyInstanceUID = PREFIX + absMostSigBits + "." + absLeastSigBits;
        studyInstanceUID = studyInstanceUID.replace("..", ".");
        studyInstanceUID = studyInstanceUID.replace("0", "1");

        return studyInstanceUID;
    }

    public static String generateIdWithPrefixAndTimestamp() {
        return PREFIX + RandomStringUtils.randomNumeric(35) + "." + Instant.now(SystemClock.get).getNano();
    }

    public static String generateTimstampsWithFormat() {
        return DATE_TIME_FORMATTER.format(LocalDateTime.now(SystemClock.get));
    }
}
