package com.bengous.e2e.common.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MimeType {
    APPLICATION_JSON("application/json"),
    TEXT_PLAIN("text/plain"),
    APPLICATION_XML("application/xml"),
    PDF("application/pdf"),
    ZIP("application/zip"),
    JPEG("image/jpeg"),
    BINARY("application/octet-stream");

    private final String value;

    public String getExtension() {
        return switch (this) {
            case APPLICATION_JSON -> ".json";
            case TEXT_PLAIN -> ".txt";
            case APPLICATION_XML -> ".xml";
            case PDF -> ".pdf";
            case ZIP -> ".zip";
            case JPEG -> ".jpeg";
            case BINARY -> "";
        };
    }
}
