package com.bengous.e2e.common.allure;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class BaseStepChain<T, S extends BaseStepChain<T, S>> {
//    protected final List<Step<T>> steps = new ArrayList<>();
//    protected StepError stepError = null;
//    protected final AllureSoftAssertions softAssertions;
//
//    protected BaseStepChain(boolean enableSoftAssertions) {
//        this.softAssertions = enableSoftAssertions ? new AllureSoftAssertions() : null;
//    }
//
////    protected abstract S addStep(String name, Step<T> step);
//
//    public void execute() {
//        T context = getInitialContext();
//        for (Step<T> step : steps) {
//            String uuid = UUID.randomUUID().toString();
//            StepResult result = new StepResult().setName(step.name());
//            Allure.getLifecycle().startStep(uuid, result);
//
//            try {
//                if (stepError != null) {
//                    log.info("STEP SKIP: \"{}\"", step.name());
//                    Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.SKIPPED));
//                } else {
//                    log.info("STEP START: \"{}\"", step.name());
//                    context = step.execute(context, softAssertions);
//                    Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.PASSED));
//                }
//            } catch (Throwable t) {
//                stepError = new StepError(step.name(), t);
//                Allure.getLifecycle().updateStep(uuid, s -> s.setStatus(Status.FAILED));
//                Allure.addAttachment("Step Failure", t.getMessage());
//                log.error("STEP ERROR: \"{}\":\n{}", step.name(), t.getMessage());
//            } finally {
//                Allure.getLifecycle().stopStep(uuid);
//                log.info("STEP END: \"{}\"", step.name());
//            }
//        }
//
//        assertNoErrors();
//        if (softAssertions != null) {
//            softAssertions.assertAll();
//        }
//    }
//
//    protected abstract T getInitialContext();
//
//    private void assertNoErrors() {
//        if (stepError != null) {
//            String message = MessageFormat.format(
//                    "Erreur à l''étape:\nStep {0}:\n{1}",
//                    stepError.stepName(),
//                    stepError.error().getMessage()
//            );
//            Assert.fail(message);
//        }
//    }
}
