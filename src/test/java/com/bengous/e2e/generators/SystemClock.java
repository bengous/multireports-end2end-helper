package com.bengous.e2e.generators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Clock;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemClock {
    public static final Clock get = Clock.systemUTC();
}
