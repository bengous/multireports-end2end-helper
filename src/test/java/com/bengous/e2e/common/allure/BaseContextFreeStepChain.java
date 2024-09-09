package com.bengous.e2e.common.allure;

import com.bengous.e2e.common.allure.interfaces.StepChain;
import com.bengous.e2e.common.allure.record.StepError;
import org.testng.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseContextFreeStepChain<S extends BaseContextFreeStepChain<S>> implements StepChain<S> {
    protected final List<Step<Void>> steps = new ArrayList<>();
    protected StepError stepError = null;

    protected abstract S self();
    protected abstract AllureSoftAssertions getSoftAssertions();

    @Override
    public void execute() {
        for (Step<Void> step : steps) {
            try {
                if (stepError != null) {
                    // Log step skip
                } else {
                    // Log step start
                    step.execute(null, getSoftAssertions());
                    // Log step pass
                }
            } catch (Throwable t) {
                stepError = new StepError(step.getName(), t);
                // Log step failure
            } finally {
                // Log step end
            }
        }

        if (getSoftAssertions() != null) {
            getSoftAssertions().assertAll();
        } else {
            assertNoErrors();
        }
    }

    private void assertNoErrors() {
        if (stepError != null) {
            String message = MessageFormat.format(
                    "Erreur à l''étape:\nStep {0}:\n{1}",
                    stepError.stepName(),
                    stepError.error().getMessage()
            );
            Assert.fail(message);
        }
    }
}