package com.bengous.e2e.common.assertions;

import com.bengous.e2e.common.json.JsonUtils;
import io.qameta.allure.Allure;
import lombok.NoArgsConstructor;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JsonAssertion {
    public static void areEquals(String stepDescription, String expected, String actual) {
        Allure.step(stepDescription, () -> {
            var formattedActual = JsonUtils.formatString(actual);
            var expectedWithIgnoredFields = JsonUtils.makeExpectedLookLikeActual(expected, actual);
            Allure.addAttachment("Expected", "application/json", expectedWithIgnoredFields);
            Allure.addAttachment("Actual", "application/json", formattedActual);
            JsonAssertions.assertThatJson(actual).isEqualTo(expectedWithIgnoredFields);
        });
    }
}
