package com.bengous.e2e.common;

import com.bengous.e2e.common.exceptions.PropertyNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

@Log4j2
public class PropertyUtils {

    private static final Properties properties = new Properties();
    private static final String CONFIG_PROPERTIES = "config.properties";

    static {
        log.info("Chargement du fichier properties \"" + CONFIG_PROPERTIES + "\" chargé.");
        try (InputStream input = ClassLoader.getSystemResourceAsStream(CONFIG_PROPERTIES)) {
            if (input == null) {
                String message = "Le fichier de configuration par défaut n'existe pas ou est mal défini: \"" + CONFIG_PROPERTIES + "\"";
                log.error(message);
                throw new IOException(message); // propager une exception sinon on ne voit pas dans les logs...
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        log.info("Fichier properties \"" + CONFIG_PROPERTIES + "\" chargé.");
    }

    public static String getProperty(String key) throws PropertyNotFoundException {
        return Optional.ofNullable(key)
                       .filter(StringUtils::isNotEmpty)
                       .flatMap(PropertyUtils::getPropertyFromSystemOrFile)
                       .orElseThrow(() -> notFoundException(key));
    }

    public static String getPropertyOrFallback(String key, String fallbackKey) throws PropertyNotFoundException {
        String value;
        try {
            value = getProperty(key);
        } catch (PropertyNotFoundException e) {
            log.warn(
                    "La property \"{}\" n'est pas valable. Tentative avec la property fallback \"{}\"",
                    key,
                    fallbackKey
            );
            value = getProperty(fallbackKey);
        }
        return value;
    }

    private static Optional<String> getPropertyFromSystemOrFile(String key) {
        return Optional.ofNullable(System.getProperty(key))
                       .or(() -> Optional.ofNullable(properties.getProperty(key)))
                       .filter(StringUtils::isNotEmpty);
    }

    private static PropertyNotFoundException notFoundException(String key) {
        String message = "La property \"" + key + "\" n'est pas définie ou est vide.";
        log.error(message);
        return new PropertyNotFoundException(message);
    }
}
