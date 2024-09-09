package com.bengous.e2e.common.client;

import com.bengous.e2e.common.client.filter.RequestLogFilter;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PartBuilder {

    public static MultiPartSpecification normal(String controlName, Object content, MimeType mimeType) throws IOException {
        return createPart(controlName, content, mimeType, false);
    }

    public static MultiPartSpecification sensitive(String controlName, Object content, MimeType mimeType) throws IOException {
        return createPart(controlName, content, mimeType, true);
    }

    private static MultiPartSpecification createPart(String controlName, Object content, MimeType mimeType, boolean isSensitive)
            throws IOException {
        final Object finalContent = getContentEvenIfString(controlName, content, mimeType);

        MultiPartSpecBuilder builder = new MultiPartSpecBuilder(finalContent)
                .controlName(controlName)
                .fileName(controlName + mimeType.getExtension())
                .mimeType(mimeType.getValue())
                .header(RequestLogFilter.IS_SENSITIVE, String.valueOf(isSensitive));

        return builder.build();
    }

    private static Object getContentEvenIfString(String controlName, Object content, MimeType mimeType) throws IOException {
        Object finalContent = content;
        if (content instanceof String string) {
            var tempFile = File.createTempFile(controlName, mimeType.getExtension());
            tempFile.deleteOnExit();
            Files.writeString(tempFile.toPath(), string);
            finalContent = tempFile;
        }
        return finalContent;
    }
}
