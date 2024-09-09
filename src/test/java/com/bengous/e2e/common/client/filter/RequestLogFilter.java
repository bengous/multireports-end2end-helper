package com.bengous.e2e.common.client.filter;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Utilitaire pour ajouter les logs de requête dans fichier de log final de chaque test.
 * Utile pour rejouer les tests manuellement avec Postman si on veut debug.
 */
public class RequestLogFilter implements Filter {
    private static final Logger log = LogManager.getLogger(RequestLogFilter.class);
    public static final String IS_SENSITIVE = "DO_NOT_LOG";

    @Override
    public Response filter(
            FilterableRequestSpecification reqSpec,
            FilterableResponseSpecification resSpec,
            FilterContext filterContext
    ) {
        Response response = filterContext.next(reqSpec, resSpec);

        log.info("Method: {}", reqSpec.getMethod());
        log.info("URI: {}", reqSpec.getURI());
        log.info("Request Headers: {}", reqSpec.getHeaders());
        log.info("Parts: {}", reqSpec.getMultiPartParams());
        log.info("Parts >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        reqSpec.getMultiPartParams().forEach(part -> {
            log.info("=============== {} ===============", part.getControlName());
            log.info("Content-Type: {}", part.getMimeType());
            // si dans le test on a oublié de préciser le flag il vaut mieux ne pas afficher la partie dans les logs
            String isSensitiveFlag = part.getHeaders().getOrDefault(IS_SENSITIVE, "true");
            boolean shouldLogIt = "true".equalsIgnoreCase(isSensitiveFlag);
            logPart(part.getContent(), shouldLogIt);
        });
        log.info("Parts <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        return response;
    }

    private void logPart(Object part, boolean isSensitive) {
        String content = part.toString();
        if (!isSensitive && part instanceof File file) {
            try {
                content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("Erreur lors de la lecture du fichier '{}'", file.getName(), e);
            }
        }

        log.info("Contenu: \n{}", isSensitive ? "*******************" : content);
    }
}