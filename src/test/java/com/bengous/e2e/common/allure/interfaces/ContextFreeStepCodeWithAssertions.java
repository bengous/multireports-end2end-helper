package com.bengous.e2e.common.allure.interfaces;

import com.bengous.e2e.common.allure.AllureSoftAssertions;

// For context-free steps with soft assertions
@FunctionalInterface
public interface ContextFreeStepCodeWithAssertions {
    void execute(AllureSoftAssertions softAssertions) throws Throwable;
}