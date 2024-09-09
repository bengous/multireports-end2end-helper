package com.bengous.e2e.common.allure.interfaces;

// For context-free steps without soft assertions
@FunctionalInterface
public interface ContextFreeStepCodeWithoutAssertions {
    void execute() throws Throwable;
}