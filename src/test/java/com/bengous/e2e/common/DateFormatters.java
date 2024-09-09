package com.bengous.e2e.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateFormatters {
    // dans le CDA les dates de début et de fin d'acte utilisent le format: <low value="18000101080101+0100"/>
    public static final DateTimeFormatter CDA_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssxx");
    // un alias pour le format ISO-8601
    public static final DateTimeFormatter ISO_8601 = DateTimeFormatter.ISO_INSTANT;
    // au format: "samedi 21 août 1024 à 08h00"
    public static final DateTimeFormatter DATE_EN_FRANCAIS = DateTimeFormatter
            .ofPattern("EEEE d MMMM yyyy 'à' HH'h'mm")
            .withLocale(Locale.FRENCH);
    public static final DateTimeFormatter CUSTOM_ISO_8601 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+01:02");
}
