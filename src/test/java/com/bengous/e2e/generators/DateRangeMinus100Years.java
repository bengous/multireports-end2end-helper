package com.bengous.e2e.generators;

import com.bengous.e2e.common.DateFormatters;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public record DateRangeMinus100Years(OffsetDateTime start, OffsetDateTime end) {
    public static DateRangeMinus100Years newInstance() {
        final var start = OffsetDateTime.now()
                                        .minusYears(100).withHour(9).withMinute(0).withSecond(0)
                                        .truncatedTo(ChronoUnit.SECONDS)
                                        .withOffsetSameInstant(ZoneOffset.ofHours(1));
        final var end = start.plusHours(1);
        return new DateRangeMinus100Years(start, end);
    }

    @Override
    public String toString() {
        return MessageFormat.format(
                "Début: {0} - Fin: {1}",
                start.format(DateFormatters.DATE_EN_FRANCAIS),
                end.format(DateFormatters.DATE_EN_FRANCAIS)
        );
    }
}
