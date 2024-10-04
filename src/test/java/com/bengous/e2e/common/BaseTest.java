package com.bengous.e2e.common;

import com.bengous.e2e.common.client.filter.RestAssuredFilters;
import com.bengous.e2e.common.exceptions.PropertyNotFoundException;
import com.bengous.e2e.common.json.JsonUtils;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Log4j2
public abstract class BaseTest {

    public static String API_URL;

    /**
     * @param apiURL         - La valeur de ce parametre est définie dans le fichier «./src/test/resources/testng/testng-*.xml »
     * @param apiURLfallback - si jamais on oublie de spécifier une URL dans la pipeline
     */
    @BeforeSuite(alwaysRun = true)
    @Parameters({"apiURL", "apiURLfallback"})
    public void setup(String apiURL, String apiURLfallback) throws IOException, PropertyNotFoundException {
        API_URL = PropertyUtils.getPropertyOrFallback(apiURL, apiURLfallback);
        RestAssured.baseURI = API_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        if (RestAssured.filters().isEmpty()) {
            RestAssured.filters(RestAssuredFilters.getFilterList());
        }

        Files.createDirectories(Paths.get(LOG_DIRECTORY));
    }

    @AfterSuite
    public void teardown() {
        JsonUtils.cleanup();
    }

    private static final String LOG_DIRECTORY = "target/logs";

    @BeforeMethod(alwaysRun = true, description = "Créer le fichier de log")
    public void setUpBeforeMethod(ITestResult result) {
        String testLogFilename;
        if (isASmokeTest(result)) {
            testLogFilename = UUID.randomUUID() + "_" + result.getMethod().getMethodName();
        } else {
            testLogFilename =
                    result.getMethod().getRealClass().getSimpleName() + "_" + result.getMethod().getMethodName();
        }
        ThreadContext.put("testLogFilename", testLogFilename);
        Path logFilePath = getTestLogPath(testLogFilename);

        try {
            Files.createFile(logFilePath);
        } catch (IOException e) {
            log.error("Erreur lors de la création du fichier de log", e);
            Allure.addAttachment("Erreur", "Erreur lors de la création du fichier de log", e.getMessage());
        }
        log.info("TEST START: \"{}\"", testLogFilename);
    }

    @AfterMethod(alwaysRun = true, description = "Servir le fichier de log")
    public void attachLogToScenario() {
        var testLogFilename = ThreadContext.get("testLogFilename");
        log.info("TEST END: \"{}\"", testLogFilename);

        Path logFilePath = getTestLogPath(testLogFilename);
        if (Files.exists(logFilePath)) {
            try (InputStream log = Files.newInputStream(logFilePath)) {
                Allure.addAttachment(testLogFilename + ".log", "text/plain", log, ".log");
            } catch (IOException e) {
                var message = "Erreur lors de la l'attachement du fichier de log" + e.getMessage();
                log.error(message);
                Allure.addAttachment(testLogFilename + ".error", "text/plain", message, ".log");
            }
        } else {
            var warn = "Fichier \"{" + logFilePath + "}\" inexistant";
            log.warn(warn);
            Allure.addAttachment(testLogFilename + ".warn", "text/plain", warn, ".log");
        }
    }

    private static Path getTestLogPath(String testLogFilename) {
        return Paths.get(LOG_DIRECTORY, testLogFilename + ".log");
    }

    private static boolean isASmokeTest(ITestResult test) {
        return Arrays.asList(test.getMethod().getGroups())
                     .contains(TestType.SMOKE);
    }
}