package com.bengous.e2e.common.allure.interfaces;

import com.bengous.e2e.common.allure.AllureSoftAssertions;

// For context-based steps with soft assertions
@FunctionalInterface
public interface ContextStepCodeWithAssertions<T> {
    void execute(T context, AllureSoftAssertions softAssertions) throws Throwable;
}
