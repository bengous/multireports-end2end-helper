package com.bengous.e2e.common.allure.interfaces;

// For context-based steps without soft assertions
@FunctionalInterface
public interface ContextStepCodeWithoutAssertions<T> {
    void execute(T context) throws Throwable;
}