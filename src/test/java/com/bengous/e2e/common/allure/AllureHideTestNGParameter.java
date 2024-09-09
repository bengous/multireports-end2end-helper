package com.bengous.e2e.common.allure;

import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.TestResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Nettoyeur de paramètres sensibles pour les rapports Allure.
 * <p>
 * Cette classe implémente l'interface TestLifecycleListener d'Allure pour intercepter
 * et nettoyer les paramètres sensibles avant qu'ils ne soient écrits dans le rapport Allure.
 * <p>
 * <strong>Note :</strong> le fichier SPI
 * src/test/resources/META-INF/services/io.qameta.allure.listener.TestLifecycleListener
 * doit contenir une référence vers cette classe pour qu'Allure prenne en compte ce listener.
 */
public class AllureHideTestNGParameter implements TestLifecycleListener {

    private static final Set<String> HIDDEN_PARAMS = new HashSet<>();

    static {
        // properties passées à BaseTest
        HIDDEN_PARAMS.add("apiURL");
        HIDDEN_PARAMS.add("apiURLfallback");
    }

    @Override
    public void beforeTestWrite(TestResult result) {
        List<Parameter> filtered = result
                .getParameters()
                .stream()
                .filter(param -> !HIDDEN_PARAMS.contains(param.getName()))
                .toList();
        result.setParameters(filtered);
    }

}