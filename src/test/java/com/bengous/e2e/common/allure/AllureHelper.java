package com.bengous.e2e.common.allure;

import io.qameta.allure.Allure;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AllureHelper {
    public static void addAttachment(String name, String type, Path filePath, String fileExtension) throws IOException {
        try (InputStream fileInputStream = Files.newInputStream(filePath)) {
            Allure.addAttachment(name, type, fileInputStream, fileExtension);
        }
    }
}
