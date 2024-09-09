package com.bengous.e2e.common.json;

import io.restassured.response.Response;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class JsonXmlExtractor {
    public static List<String> extractXmlNodeFromJsonPath(
            Response response, String pathToExtractFrom, String nodeName
    ) {
        return response
                .jsonPath()
                .getList(pathToExtractFrom, String.class)
                .stream()
                .map(r -> JsonXmlExtractor.extractNodeByName(nodeName, r))
                .filter(Objects::nonNull)
                .toList();
    }

    private static String extractNodeByName(String nodeName, String toExtractFrom) {
        String pattern = "<" + nodeName + ".*</" + nodeName + ">";
        Pattern nodePattern = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matcher = nodePattern.matcher(toExtractFrom);
        return matcher.find() ? matcher.group() : null;
    }
}
