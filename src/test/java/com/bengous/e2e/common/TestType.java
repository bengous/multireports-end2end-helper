package com.bengous.e2e.common;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TestType {
    public static final String SMOKE = "smoke";
    public static final String SIMPLE = "simple";
    public static final String SCENARIO = "scenario";

    public static String[] getAllTypes() {
        return new String[]{SMOKE, SIMPLE, SCENARIO};
    }
}
