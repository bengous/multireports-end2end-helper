package com.bengous.e2e.common.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StatusDetails;
import io.qameta.allure.model.StepResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Utilitaire permettant de créer une chaîne d'étapes de test avec gestion du contexte et rapports Allure.
 *
 * @param <T> Le type du contexte utilisé dans la chaîne
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StepChain<T> {

    /**
     * Représentation d'une étape dans la chaîne de test.
     * Fournit une abstraction pour différents types d'étapes, permettant une exécution uniforme.
     *
     * @param <T> Le type du contexte utilisé dans l'étape.
     */
    private interface Step<T> {
        String name();

        // pour activer ou non le reporting dans Allure
        boolean shouldReport();

        /**
         * Exécute l'étape avec le contexte donné.
         *
         * @param context Le contexte actuel.
         * @return Le contexte mis à jour après l'exécution de l'étape.
         * @throws Throwable Si une erreur se produit pendant l'exécution de l'étape.
         */
        T execute(T context) throws Throwable;
    }

    // Implémentation de Step pour les étapes qui utilisent et modifient le contexte.
    private record ContextStep<T>(
        String name, boolean shouldReport, Allure.ThrowableContextRunnable<T, T> code
    ) implements Step<T> {
        @Override
        public T execute(T context) throws Throwable {
            return code.run(context);
        }
    }

    // Implémentation de Step pour les étapes qui utilisent le contexte sans le modifier.
    private record VoidContextStep<T>(
        String name, boolean shouldReport, Allure.ThrowableContextRunnableVoid<T> code
    ) implements Step<T> {
        @Override
        public T execute(T context) throws Throwable {
            code.run(context);
            return context;
        }
    }

    // Implémentation de Step pour les étapes qui n'utilisent pas le contexte et ne le modifient pas.
    private record ContextFreeStep<T>(
        String name, boolean shouldReport, Allure.ThrowableRunnableVoid code
    ) implements Step<T> {
        @Override
        public T execute(T context) throws Throwable {
            code.run();
            return context;
        }
    }

    private record StepError(String stepName, String errorTitle, Throwable error) {/**/}

    private final List<Step<T>> steps = new ArrayList<>();
    private StepError stepError = null;
    private T initialContext;

    public StepChain(T context) {
        this.initialContext = context;
    }

    // Ajoute une étape qui utilise et modifie le contexte.
    public StepChain<T> runStep(String name, Allure.ThrowableContextRunnable<T, T> code) {
        steps.add(new ContextStep<>(name, true, code));
        return this;
    }

    // FIXME: bug when an updated context is passed in the lambda call (eg. wither pattern)
    // Ajoute une étape qui utilise le contexte, mais ne le modifie pas.
    public StepChain<T> runVoidStep(String name, Allure.ThrowableContextRunnableVoid<T> code) {
        steps.add(new VoidContextStep<>(name, true, code));
        return this;
    }

    public StepChain<T> runContextFreeStep(String name, Allure.ThrowableRunnableVoid code) {
        steps.add(new ContextFreeStep<>(name, true, code));
        return this;
    }
    public StepChain<T> runContextFreeStep(String name, boolean shouldReport, Allure.ThrowableRunnableVoid code) {
        steps.add(new ContextFreeStep<>(name, shouldReport, code));
        return this;
    }

    // Exécute toutes les étapes définies dans la chaîne.
    public T execute() {
        T context = this.initialContext;
        for (Step<T> step : steps) {
            String uuid = null;
            if (step.shouldReport()) {
                uuid = UUID.randomUUID().toString();
                StepResult result = new StepResult().setName(step.name());
                Allure.getLifecycle().startStep(uuid, result);
            }

            try {
                if (stepError != null) {
                    log.info("STEP SKIP: \"{}\"", step.name());
                    if (step.shouldReport()) {
                        Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.SKIPPED));
                    }
                } else {
                    log.info("STEP START: \"{}\"", step.name());
                    context = step.execute(context);
                    if (step.shouldReport()) {
                        Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.PASSED));
                    }
                }
            } catch (Throwable t) {
                stepError = new StepError(step.name(), firstLine(t.getMessage()), t);
                if (step.shouldReport()) {
                    Allure.addAttachment("Fichier log erreur", "text/plain", streamStackTrace(t), ".error");
                    Allure.getLifecycle().updateStep(uuid, s -> {
                        s.setStatus(Status.FAILED);
                        var details = new StatusDetails()
                                .setMessage(stepError.errorTitle)
                                .setTrace(stepError.error.getMessage());
                        s.setStatusDetails(details);
                    });
                }
                log.error("STEP ERROR: \"{}\":\n{}", step.name(), t.getMessage());
            } finally {
                if (step.shouldReport()) {
                    Allure.getLifecycle().stopStep(uuid);
                }
                log.info("STEP END: \"{}\"", step.name());
            }
        }

        assertNoErrors();

        return context;
    }

    private void assertNoErrors() {
        if (stepError != null) {
            String message = MessageFormat.format(
                "Erreur à l''étape:\nStep {0}:\n{1}",
                stepError.stepName,
                stepError.error.getMessage()
            );
            Assert.fail(message);
        }
    }

    private String firstLine(String message) {
        return Stream.of(message.split("\n")).findFirst().orElse("Erreur");
    }

    private String streamStackTrace(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}