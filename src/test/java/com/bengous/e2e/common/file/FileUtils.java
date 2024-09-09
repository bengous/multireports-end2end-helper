package com.bengous.e2e.common.file;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {
    private static final ClassLoader classLoader = FileUtils.class.getClassLoader();

    public static File getResourceFile(String resourceName) throws IOException {
        URL resourceUrl = classLoader.getResource(resourceName);
        if (resourceUrl == null) {
            throw new FileNotFoundException("File not found: " + resourceName);
        }
        try {
            return Path.of(resourceUrl.toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new IOException("Invalid resource URI: " + resourceName, e);
        }
    }

    public static File loadTemplateAndReplaceWildcards(
            String templatePath, Map<String, String> values
    ) throws IOException {
        String template = readFile(templatePath);
        StringSubstitutor sub = new StringSubstitutor(values, "{{", "}}");
        String templateWithReplacements = sub.replace(template);
        return writeToTempFile(templateWithReplacements);
    }

    public static String readFile(String resourcePath) throws IOException {
        URL resourceUrl = classLoader.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new FileNotFoundException("File not found: " + resourcePath);
        }
        try {
            Path path = Path.of(resourceUrl.toURI());
            return Files.readString(path);
        } catch (URISyntaxException e) {
            throw new IOException("Invalid resource URI: " + resourcePath, e);
        }
    }

    public static File writeToTempFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("temp_file___" + UUID.randomUUID(), "");
        Files.writeString(tempFile, content, StandardCharsets.UTF_8);
        tempFile.toFile().deleteOnExit();
        return tempFile.toFile();
    }
}
