package com.bengous.e2e.common.allure;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
public class AllureSoftAssertions {
    private record StepError(String stepName, Throwable error) {
        /**/
    }

    private record SoftAssertion(String name, Allure.ThrowableRunnableVoid code) {
        public void run() throws Throwable {
            code.run();
        }
    }

    private final List<SoftAssertion> assertions = new ArrayList<>();
    private final List<StepError> errors = new ArrayList<>();

    public AllureSoftAssertions wrapAssertion(String name, Allure.ThrowableRunnableVoid code) {
        assertions.add(new SoftAssertion(name, code));
        return this;
    }

    public void assertAll() {
        for (SoftAssertion assertion : assertions) {
            String uuid = UUID.randomUUID().toString();
            StepResult result = new StepResult().setName(assertion.name());
            Allure.getLifecycle().startStep(uuid, result);

            try {
                log.info("ASSERTION START: \"{}\"", assertion.name());
                assertion.run();
                Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.PASSED));
            } catch (Throwable t) {
                errors.add(new StepError(assertion.name(), t));
                Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.FAILED));
                Allure.addAttachment("Assertion Failure", t.getMessage());
                log.error("ASSERTION ERROR: \"{}\":\n{}", assertion.name(), t.getMessage());
            } finally {
                Allure.getLifecycle().stopStep(uuid);
                log.info("ASSERTION END: \"{}\"", assertion.name());
            }
        }

        if (!errors.isEmpty()) {
            throw new AssertionError(errors.size() + " erreurs lors du test.\n");
        }
    }
}