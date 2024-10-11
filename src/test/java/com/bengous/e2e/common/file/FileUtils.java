package com.bengous.e2e.common.file;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {
    private static final ClassLoader CLASS_LOADER = FileUtils.class.getClassLoader();

    public static Path pathFrom(String resourceName) throws IOException {
        URL resourceUrl = CLASS_LOADER.getResource(resourceName);
        if (resourceUrl == null) {
            throw new FileNotFoundException("File not found: " + resourceName);
        }
        try {
            return Path.of(resourceUrl.toURI());
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
        URL resourceUrl = CLASS_LOADER.getResource(resourcePath);
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

    // TODO: rm `public static String readFile(String resourcePath) throws IOException` or not
    public static String readResourceAsString(String resourceName) throws IOException {
        try (InputStream inputStream = CLASS_LOADER.getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + resourceName);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static File writeToTempFile(String content) throws IOException {
        Path tempFile = Files.createTempFile("temp_file___" + UUID.randomUUID(), "");
        Files.writeString(tempFile, content, StandardCharsets.UTF_8);
        tempFile.toFile().deleteOnExit();
        return tempFile.toFile();
    }

    public static File loadTemplateAndReplaceWildcards(
            Path template, Map<String, String> values
    ) throws IOException {
        String str = Files.readString(template);
        StringSubstitutor sub = new StringSubstitutor(values, "{{", "}}");
        final String templateWithReplacements = sub.replace(str);
        return writeToTempFile(templateWithReplacements);
    }

    public static String stringFromDynamicTemplate(Path template, Map<String, String> values) throws IOException {
        String str = Files.readString(template);
        StringSubstitutor sub = new StringSubstitutor(values, "{{", "}}");
        return sub.replace(str);
    }
}
